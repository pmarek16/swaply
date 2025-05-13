# Zadání:
Vytvořte Spring Boot aplikaci (Kotlin případně Java), která bude mít následující vlastnosti:
 - Bude fungovat jako samostatná služba bez runtime závislostí, pokud není specifikováno jinak.
 - Bude poskytovat funkcionalitu pro získání kurzového lísku na základě ČNB 
   - https://www.cnb.cz/cs/financni_trhy/devizovy_trh/kurzy_devizoveho_trhu/denni_kurz.txt 
   - nebo jako XML https://www.cnb.cz/cs/financni_trhy/devizovy_trh/kurzy_devizoveho_trhu/denni_kurz.xml
   - a jednoho externího API se seznamu na githubu https://github.com/public-apis/public-apis?tab=readme-ov-file#currency-exchange
 - Implementuje následující 2 API
   - Seznam podporovaných párů měn
   - Pro měnový pár vrátí rozdíl v kurzu mezi ČNB a vybraným providerem.
 - Služba poskytuje healthcheck
 - Služba bude zabezpečená pomocí Basic Auth nebo Oauth (použijte případně Google jako OAuth server)

# Řešení
Příklady volání jsou v souboru [swaply.http](http-client/swaply.http)
 - healthcheck endpoint - GET [/actuator/health](http://localhost:9000/actuator/health)
 - API Seznam podporovaných párů měn - GET [/exchange-rate/currency-pairs](http://localhost:8080/api/v1/exchange-rate/currency-pairs)
 - API Swaply aplikace je vystavené na URL - GET [/api-docs](http://localhost:8080/api-docs)
 - API Pro měnový pár vrátí rozdíl v kurzu mezi ČNB a vybraným providerem - GET [/exchange-rate/differences/{{currencyPairCode}}](http://127.0.0.1:8080/api/v1/exchange-rate/differences/CZKHUF)
 - Služba bude zabezpečená pomocí Basic Auth nebo... - doplněna autorizace k requestům v HTTP souboru
