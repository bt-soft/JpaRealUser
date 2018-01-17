# RealJpaUser
Test the transfer of the user ID to the Oracle database when using connection pool


## A feladat

Adott egy korábbi, több instance-ból álló Oracle RAC adatbázison(DB) alapuló rendszer, Oracle Forms felhasználói felülettel, sok ezer interaktív felhasználóval, háttéeben futó batch jellegű job-okkal, stb.
Ehhez a környezethez kell illeszteni egy olyan újabb JEE7/JSF/JPA archítektúrát - ami már szintén rendelkezik viszonlag számos alkalmazással - úgy, hogy a két alkalmazástér továbbra is práhuzamosan működjön tovább.

A jelenleg megoldandó problémát az jelenti, hogy a korábbi rendszerek olyan DB trigger (after insert or update or delete) alapú audit megoldást használnak, ami az adatbázis `user` SQL függvény segítségével
állapítja meg az aktuális felhasználót. A JEE környezetben a JPA pooling miatt az Alkalmazásszerver (AS) és a DB között technikai user van, így egy módosító SQL esetén a DB triggerek a technikai user account-ját
írják be az audit táblamezőkbe a tényleges felhasználó helyett.

### A környezet

| Komponens | Technológia | Implementáció |
|-----------|-------------|---------------|
| DB | Oracle | Oracle 11g |
| AS | JEE7 | GalssFish 4.1x/5|
| ORM | JPA | EclipseLink 6.1.2/7.1.2 |
| UI | JSF  | JSF 2.1 + PrimeFaces 6.1|


## A lehetséges megoldás


