/*
 *  ------------------------------------------------------------------------------------
 *
 *  JPA Real User Test Monitor project
 *
 *  Module:  JruTest (JruTest)
 *  File:    VersionUtils.java
 *  Created: 2017.12.23. 8:33:06
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.jru.core.version;

import com.github.zafarkhaja.semver.Version;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;

/**
 * Modulok verzióinformációinak kezelése
 *
 * @author BT
 */
@Slf4j
public class VersionUtils {

    /**
     * A Maven által karban tartott verzió resource file neve
     */
    private final static String VERSION_FILE_NAME = "/versionInfo.properties";

    /**
     * A hívó modul /versionInfo.properties állományából olvassa fel a Maven
     * által karbantartott verzió információt
     *
     * @param clazz a hívó fél osztálya
     *
     * @return Stringes verzió
     */
    public static String getModuleVersionStr(Class clazz) {

        try (InputStream is = clazz.getResourceAsStream(VERSION_FILE_NAME)) {
            if (is != null) {

                Properties p = new Properties();
                p.load(is);

                return p.getProperty("version");
            } else {
                log.error("Nem olvasható fel a(z) {} osztály moduljának {} állománya! (InputStream == null)", clazz.getName(), VERSION_FILE_NAME);
            }
        } catch (IOException e) {
            log.error("Nem található fel a versionInfo.properties állomány", e);
        }

        return null;
    }

    /**
     * A modul verziójának lekérdezése
     *
     * @param clazz a hívó modul osztálya
     *
     * @return Version verzió
     */
    public static Version getModuleVersion(Class clazz) {
        return Version.valueOf(getModuleVersionStr(clazz));
    }

    /**
     * Verzió komparálás minimális verzió teljesülésére
     *
     * @param module  a verzióra összehasonlítandó modul neve
     * @param minV    elvárt minimum verzio
     * @param curentV aktuális verzió
     *
     * @throws IllegalStateException ha nincs meg az elvárt verzió
     */
    public static void minCompare(String module, String minV, String curentV) throws IllegalStateException {
        Version minVersion = Version.valueOf(minV);
        Version currentVersion = Version.valueOf(curentV);
        if (currentVersion.lessThan(minVersion)) {
            throw new IllegalStateException(String.format("%s: Az elvárt verzió (%s) nagyobb, mint az aktuális verzió (%s)!", module, minVersion, currentVersion));
        }
    }

    /**
     * Verzió komparálás minimális verzió teljesülésére
     *
     * @param module     a verzióra összehasonlítandó modul neve
     * @param mandatoryV kötelezően elvárt verzio
     * @param curentV    aktuális verzió
     *
     * @throws IllegalStateException ha nincs meg az elvárt verzió
     */
    public static void equCompare(String module, String mandatoryV, String curentV) throws IllegalStateException {
        Version mandtryVersion = Version.valueOf(mandatoryV);
        Version currentVersion = Version.valueOf(curentV);
        if (!mandtryVersion.equals(currentVersion)) {
            throw new IllegalStateException(String.format("%s: A kötelezően elvárt pontos verzió (%s) nem egyezik az aktuális verzióval (%s)!", module, mandtryVersion, currentVersion));
        }
    }

}
