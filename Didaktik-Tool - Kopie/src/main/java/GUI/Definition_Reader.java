package GUI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Definition_Reader {
	
	public String getDefinitionForSymbol(String symbol, String source_name, String not_found_string) {
        
        // Hole das aktuelle Arbeitsverzeichnis
        String currentDir = System.getProperty("user.dir");
        File file = new File(currentDir + "/" + source_name); // Datei relativ zum Verzeichnis der JAR
        
        //System.out.println("Versuche, die Datei zu laden von: " + file.getAbsolutePath());
        
        // Fallback für Ressourcen innerhalb des JARs
        if (!file.exists()) {
            //System.out.println("Datei nicht gefunden, versuche es innerhalb des JARs zu laden: " + source_name);
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(source_name);
            if (inputStream == null) {
                System.err.println("Datei nicht gefunden: " + source_name);
                return not_found_string;
            }
            
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))) {
                return leseDefinitionAusDatei(reader, symbol, not_found_string);
            } catch (IOException e) {
                e.printStackTrace();
                return not_found_string;
            }
        }
        
        // Wenn die Datei existiert, lese sie direkt vom Dateisystem
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
            return leseDefinitionAusDatei(reader, symbol, not_found_string);
        } catch (IOException e) {
            System.err.println("Fehler beim Laden der Datei: " + file.getAbsolutePath());
            e.printStackTrace();
        }
        
        return not_found_string;
    }

    // Hilfsmethode, um den Definitionsteil zu extrahieren
    private String leseDefinitionAusDatei(BufferedReader reader, String symbol, String not_found_string) throws IOException {
        StringBuilder currentDefinition = new StringBuilder();
        String line;
        boolean isDefinitionFound = false;

        while ((line = reader.readLine()) != null) {
            // Prüfe, ob die Zeile das Symbol enthält und mit ":" gefolgt wird
            if (line.startsWith(symbol + ":")) {
                isDefinitionFound = true; // Definition beginnt
                int commentIndex = line.indexOf("//");
                if (commentIndex != -1) {
                    // Wenn "//" in der gleichen Zeile ist, trenne hier und beende das Sammeln der Definition
                    currentDefinition.append(line.substring(line.indexOf(":") + 1, commentIndex).trim());
                    break;
                } else {
                    currentDefinition.append(line.substring(line.indexOf(":") + 1).trim()).append("\n");
                }
            } 
            // Falls eine Definition gefunden wurde, füge die nächsten Zeilen hinzu
            else if (isDefinitionFound) {
                int commentIndex = line.indexOf("//");
                if (commentIndex != -1) {
                    // Falls "//" in der aktuellen Zeile gefunden wird, füge nur den Teil vor "//" hinzu und beende das Sammeln
                    currentDefinition.append(line.substring(0, commentIndex).trim());
                    break;
                } else {
                    currentDefinition.append(line.trim()).append("\n"); // Füge jede Zeile der Definition hinzu
                }
            }
        }

        String definition = currentDefinition.toString().trim(); // Entferne unnötige Leerzeichen und Zeilenumbrüche am Ende
        return definition.isEmpty() ? not_found_string : definition;
    }
}
