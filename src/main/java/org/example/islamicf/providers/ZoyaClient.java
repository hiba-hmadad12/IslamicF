// src/main/java/org/example/islamicf/providers/ZoyaClient.java
package org.example.islamicf.providers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.islamicf.dto.ScreeningSnapshotDTO;
import org.example.islamicf.entities.Company;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ZoyaClient implements ScreeningClient {

    @Value("${apis.zoya.base-url}")
    private String baseUrl;   // ex: https://sandbox-api.zoya.finance/graphql

    @Value("${apis.zoya.key}")
    private String apiKey;

    @Value("${apis.zoya.mock:false}")
    private boolean mock;

    private final RestClient http = RestClient.create();
    private final ObjectMapper om = new ObjectMapper();

    // ==== GraphQL queries (Advanced + Basic) =================================
    private static final String ADV_QUERY = """
      query GetAdvancedReport($symbol: String!) {
        advancedCompliance {
          report(input: { symbol: $symbol, methodology: AAOIFI }) {
            symbol rawSymbol name figi exchange status reportDate
            businessScreen financialScreen
            compliantRevenue nonCompliantRevenue questionableRevenue
            ... on AAOIFIReport {
              securitiesToMarketCapRatio
              debtToMarketCapRatio
            }
          }
        }
      }
      """;

    private static final String BASIC_QUERY = """
      query GetReport($symbol: String!) {
        basicCompliance {
          report(symbol: $symbol) {
            symbol name exchange status reportDate purificationRatio
          }
        }
      }
      """;

    private static final String ALL_COMPANIES_QUERY = """
      query GetAllCompanies($input: BasicReportsInput) {
        basicCompliance {
          reports(input: $input) {
            items {
              symbol
              name
              exchange
              status
              reportDate
            }
            nextToken
          }
        }
      }
      """;

    @Override
    public String source() { return "ZOYA"; }

    @Override
    public ScreeningSnapshotDTO fetchAndNormalize(Company company, String apiKeyOverride) {
        if (mock) return mockDto(company, "mock enabled");

        final String key = (apiKeyOverride != null && !apiKeyOverride.isBlank()) ? apiKeyOverride : apiKey;
        final String symbol = company.getSymbol();
        log.info("Zoya call: mock={}, baseUrl={}, keyLen={}, company={}, symbol={}",
                mock, baseUrl, (key == null ? 0 : key.length()), company.getId(), symbol);

        try {
            // 1) Tenter ADVANCED
            JsonNode root;
            boolean usedAdvanced = true;
            try {
                root = callZoya(ADV_QUERY, Map.of("symbol", symbol), key);
                if (hasGraphQLErrors(root)) {
                    log.warn("Zoya Advanced returned GraphQL errors → fallback Basic: {}", root.path("errors"));
                    usedAdvanced = false;
                    root = callZoya(BASIC_QUERY, Map.of("symbol", symbol), key);
                }
            } catch (RestClientResponseException ex) {
                // 401/403/… côté Advanced → on bascule en Basic
                log.warn("Zoya Advanced HTTP {} → fallback Basic", ex.getRawStatusCode());
                usedAdvanced = false;
                root = callZoya(BASIC_QUERY, Map.of("symbol", symbol), key);
            }

            // 2) Normalisation selon la branche utilisée
            if (usedAdvanced) {
                JsonNode report = root.path("data").path("advancedCompliance").path("report");
                if (report.isMissingNode() || report.isNull()) {
                    return mockDto(company, "advanced: report missing");
                }

                // Revenus (0..1)
                BigDecimal compliantRevenue    = bd(report.path("compliantRevenue"));
                BigDecimal nonCompliantRevenue = bd(report.path("nonCompliantRevenue"));
                BigDecimal questionableRevenue = bd(report.path("questionableRevenue"));
                BigDecimal total = compliantRevenue.add(nonCompliantRevenue).add(questionableRevenue);

                BigDecimal haramRevenuePct = total.signum() == 0
                        ? BigDecimal.ZERO
                        : nonCompliantRevenue.divide(total, 6, RoundingMode.HALF_UP);

                // Ratios financiers
                BigDecimal cashAndInterestPct = bd(report.path("securitiesToMarketCapRatio")); // ~ cash & placements / mkt cap
                BigDecimal interestDebtPct    = bd(report.path("debtToMarketCapRatio"));       // ~ dettes portant intérêt / mkt cap
                BigDecimal interestIncomePct  = null; // non exposé par le schéma actuel
                // NEW: compliant (0..1)
                BigDecimal compliantRevenuePct = total.signum()==0 ? null
                        : compliantRevenue.divide(total, 6, RoundingMode.HALF_UP);

                // On dérive un "purificationRatio" approximatif = part non conforme
                BigDecimal purificationRatio  = (total.signum() == 0) ? null
                        : nonCompliantRevenue.divide(total, 6, RoundingMode.HALF_UP);

                String zoyaStatus  = report.path("status").asText("UNKNOWN");
                String business    = report.path("businessScreen").asText("");
                String financial   = report.path("financialScreen").asText("");
                String reportDate  = report.path("reportDate").asText("");

                String status = normalizeStatus(zoyaStatus);

                String notes = "Zoya Advanced (AAOIFI) — status=%s, business=%s, financial=%s, reportDate=%s"
                        .formatted(zoyaStatus, business, financial, reportDate);

                return ScreeningSnapshotDTO.builder()
                        .companyId(company.getId())
                        .source(source())
                        .fetchedAt(Instant.now())
                        .status(status)
                        .notes(notes)
                        .haramRevenuePct(haramRevenuePct)
                        .interestDebtPct(interestDebtPct)
                        .interestIncomePct(interestIncomePct)
                        .cashAndInterestPct(cashAndInterestPct)
                        .purificationRatio(purificationRatio)
                        .compliantRevenuePct(compliantRevenuePct)
                        .build();

            } else {
                JsonNode report = root.path("data").path("basicCompliance").path("report");
                if (report.isMissingNode() || report.isNull()) {
                    return mockDto(company, "basic: report missing");
                }

                String zoyaStatus  = report.path("status").asText("UNKNOWN");
                String reportDate  = report.path("reportDate").asText("");

                String status = normalizeStatus(zoyaStatus);

                // Basic : expose 'purificationRatio' si présent, sinon null
                BigDecimal purificationRatio = bdOrNull(report.path("purificationRatio"));

                String notes = "Zoya Basic — status=%s, reportDate=%s%s"
                        .formatted(
                                zoyaStatus,
                                reportDate,
                                purificationRatio != null ? ", purification=" + purificationRatio : ""
                        );

                return ScreeningSnapshotDTO.builder()
                        .companyId(company.getId())
                        .source(source())
                        .fetchedAt(Instant.now())
                        .status(status)
                        .notes(notes)
                        // Basic: pas de détails financiers → null
                        .haramRevenuePct(null)
                        .interestDebtPct(null)
                        .interestIncomePct(null)
                        .cashAndInterestPct(null)
                        .purificationRatio(purificationRatio)
                        .compliantRevenuePct(null)
                        .build();
            }

        } catch (Exception ex) {
            log.warn("Zoya GraphQL failed → fallback mock. reason={}", ex.getMessage());
            return mockDto(company, "zoya call failed: " + ex.getMessage());
        }
    }

    // ==== HTTP / JSON utils ===================================================
    private JsonNode callZoya(String query, Map<String, Object> variables, String key) throws Exception {
        ResponseEntity<String> res = http.post()
                .uri(baseUrl) // e.g. https://sandbox-api.zoya.finance/graphql
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, key) // IMPORTANT: pas "Bearer", juste la clé sandbox
                .body(Map.of("query", query, "variables", variables))
                .retrieve()
                .toEntity(String.class);

        String body = res.getBody();
        log.info("Zoya HTTP {} → {}", res.getStatusCode().value(), truncate(body, 900));
        return om.readTree(body);
    }

    private static boolean hasGraphQLErrors(JsonNode root) {
        return root.has("errors") && root.get("errors").isArray() && root.get("errors").size() > 0;
    }

    private static String normalizeStatus(String zoyaStatus) {
        String s = (zoyaStatus == null) ? "UNKNOWN" : zoyaStatus.toUpperCase();
        return switch (s) {
            case "COMPLIANT", "PASS", "HALAL" -> "HALAL";
            case "NON_COMPLIANT", "FAIL", "HARAM" -> "HARAM";
            case "REVIEW", "QUESTIONABLE", "MIXED" -> "MIXED";
            default -> "UNKNOWN";
        };
    }

    private static BigDecimal bd(JsonNode node) {
        try {
            if (node == null || node.isNull() || node.isMissingNode()) return BigDecimal.ZERO;
            if (node.isNumber()) return node.decimalValue();
            String s = node.asText();
            if (s == null || s.isBlank()) return BigDecimal.ZERO;
            return new BigDecimal(s);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    private static BigDecimal bdOrNull(JsonNode node) {
        try {
            if (node == null || node.isNull() || node.isMissingNode()) return null;
            if (node.isNumber()) return node.decimalValue();
            String s = node.asText();
            if (s == null || s.isBlank()) return null;
            return new BigDecimal(s);
        } catch (Exception e) {
            return null;
        }
    }

    private static String truncate(String s, int max) {
        if (s == null) return "null";
        return s.length() > max ? s.substring(0, max) + " …" : s;
    }


    public JsonNode fetchRawData(Company company, String apiKeyOverride) {
        if (mock) {
            try {
                return om.readTree("{\"mock\":true,\"reason\":\"mock enabled\",\"company\":\"" + company.getSymbol() + "\"}");
            } catch (Exception e) {
                return null;
            }
        }

        final String key = (apiKeyOverride != null && !apiKeyOverride.isBlank()) ? apiKeyOverride : apiKey;
        final String symbol = company.getSymbol();
        log.info("Zoya raw call: mock={}, baseUrl={}, keyLen={}, company={}, symbol={}",
                mock, baseUrl, (key == null ? 0 : key.length()), company.getId(), symbol);

        try {

            JsonNode root = callZoya(ADV_QUERY, Map.of("symbol", symbol), key);


            if (hasGraphQLErrors(root)) {
                log.warn("Zoya Advanced returned GraphQL errors → fallback Basic: {}", root.path("errors"));
                root = callZoya(BASIC_QUERY, Map.of("symbol", symbol), key);
            }

            return root;
        } catch (RestClientResponseException ex) {

            log.warn("Zoya Advanced HTTP {} → fallback Basic", ex.getRawStatusCode());
            try {
                return callZoya(BASIC_QUERY, Map.of("symbol", symbol), key);
            } catch (Exception e) {
                log.error("Both Zoya queries failed: {}", e.getMessage());
                return null;
            }
        } catch (Exception ex) {
            log.error("Zoya GraphQL failed: {}", ex.getMessage());
            return null;
        }
    }


    public JsonNode fetchAllCompanies(Integer limit, String nextToken, String apiKeyOverride) {
        if (mock) {
            // Create a simple mock response for testing
            try {
                return om.readTree("{\"mock\":true,\"reason\":\"mock enabled\",\"message\":\"All companies data\"}");
            } catch (Exception e) {
                return null;
            }
        }

        final String key = (apiKeyOverride != null && !apiKeyOverride.isBlank()) ? apiKeyOverride : apiKey;
        log.info("Zoya fetchAllCompanies call: mock={}, baseUrl={}, keyLen={}, limit={}, hasNextToken={}",
                mock, baseUrl, (key == null ? 0 : key.length()), limit, (nextToken != null));

        try {

            Map<String, Object> inputMap = new HashMap<>();
            inputMap.put("limit", limit != null ? limit : 20);
            inputMap.put("nextToken", nextToken);


            JsonNode root = callZoya(ALL_COMPANIES_QUERY, Map.of("input", inputMap), key);


            if (hasGraphQLErrors(root)) {
                log.warn("Zoya fetchAllCompanies returned GraphQL errors: {}", root.path("errors"));
                return null;
            }

            return root;
        } catch (RestClientResponseException ex) {
            log.error("Zoya fetchAllCompanies HTTP error: {} - {}", ex.getRawStatusCode(), ex.getMessage());
            return null;
        } catch (Exception ex) {
            log.error("Zoya fetchAllCompanies failed: {}", ex.getMessage());
            return null;
        }
    }


    public List<org.example.islamicf.dto.CompanyWithStatusDTO> fetchAndMapCompaniesWithStatus(Integer limit, String nextToken, String apiKeyOverride) {
        // First get the raw JSON data
        JsonNode rawData = fetchAllCompanies(limit, nextToken, apiKeyOverride);

        if (rawData == null) {
            log.error("Failed to fetch companies data from Zoya API");
            return null;
        }

        // Extract the items array from the response
        JsonNode items = rawData.path("data").path("basicCompliance").path("reports").path("items");
        if (items.isMissingNode() || items.isNull() || !items.isArray()) {
            log.warn("Zoya API response doesn't contain expected data structure");
            return null;
        }

        // Map each item to a CompanyWithStatusDTO object
        List<org.example.islamicf.dto.CompanyWithStatusDTO> companies = new ArrayList<>();
        for (JsonNode item : items) {
            String symbol = item.path("symbol").asText();
            String name = item.path("name").asText();
            String exchange = item.path("exchange").asText();
            String zoyaStatus = item.path("status").asText("UNKNOWN");

            // Normalize the status using the existing method
            String normalizedStatus = normalizeStatus(zoyaStatus);

            // Create a new Company object
            Company company = Company.builder()
                    .symbol(symbol)
                    .name(name)
                    .sector(exchange) // Using exchange as sector for now
                    .country("Unknown") // Default country
                    .build();

            // Convert to CompanyWithStatusDTO and add the status
            org.example.islamicf.dto.CompanyWithStatusDTO companyWithStatus = 
                org.example.islamicf.dto.CompanyWithStatusDTO.from(company, normalizedStatus);
            companies.add(companyWithStatus);
        }

        log.info("Successfully mapped {} companies from Zoya API with status", companies.size());
        return companies;
    }

    // ==== Mock ================================================================
    private ScreeningSnapshotDTO mockDto(Company c, String note) {
        return ScreeningSnapshotDTO.builder()
                .companyId(c.getId())
                .source(source())
                .fetchedAt(Instant.now())
                .status("HALAL")
                .notes(note)
                .haramRevenuePct(new BigDecimal("0.010"))   // 1.0%
                .interestDebtPct(new BigDecimal("0.030"))   // 3.0%
                .interestIncomePct(null)

                .build();
    }
}
