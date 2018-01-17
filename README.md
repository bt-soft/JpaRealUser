# RealJpaUser
Test the transfer of the user ID to the Oracle database when using connection pool


## A feladat

Adott egy korábbi, több instance-s Oracle RAC adatbázison(DB) alapuló rendszer, Oracle Forms felhasználói felülettel, sok ezer interaktív felhasználóval, adatbázis job-okkal, stb.
Ehhez a környezethez kell illeszteni egy olyan újabb JEE/JSF/JPA archítektúrát, ami már szintén rendelkezik viszonlag számos alkalmazással.

A megoldandó problémát az jelenti, hogy a korábbi rendszerek olyan DB trigger (*after Insert or update or delete*) alapú audit megoldást használnak, ami az adatbázis `user` SQL függvény segítségével
állapítja meg az aktuális felhasználót. Az új JEE környezetben a JPA pooling miatt az Alkalmazásszerver (AS) és a DB között technikai user van, így a DB triggerek a technikai user account-ját írják be az audit táblamezőkbe a tényleges felhasználó helyett.
