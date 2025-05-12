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
 - healthcheck endpoint - příklad volání je v souboru [swaply.http](http-client/swaply.http) 
