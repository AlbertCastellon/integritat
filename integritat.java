import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Scanner;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.security.SecureRandom;


public class integritat {

    public static void main(String[] args) {
        integritat p = new integritat();
        p.principal();
    }
    Scanner sc = new Scanner(System.in);
    ArrayList<String> users = new ArrayList<String>();
    ArrayList<String> passwords = new ArrayList<String>();
    ArrayList<String> salts = new ArrayList<String>();
    SecureRandom random = new SecureRandom();

    public void principal() {
        int ex;
        do {
            System.out.println("1. Exercici 1");
            System.out.println("2. Exercici 2");
            System.out.println("3. Exercici 3");
            System.out.println("4. Exercici 4");
            System.out.println("0. Sortir");
            ex = sc.nextInt();
            sc.nextLine();

            switch (ex) {
                case 1:
                    System.out.println("Escriu missatge 1");
                    String missatge1 = sc.nextLine();
                    String hash1 = getHash(missatge1);
                    System.out.println("Escriu missatge 2");
                    String missatge2 = sc.nextLine();
                    String hash2 = getHash(missatge2);
                    if(hash1.equals(hash2)) {
                        System.out.println("El hash coincideix per tant el missatge és el mateix");
                    }else {
                        System.out.println("El hash no coincideix per tant el missatge és diferent");
                    }
                    break;
                case 2:
                    checkMissatge();
                    break;
                case 3:
                    registrarUsuari();
                    break;
                case 4:
                    iniciSessio();
                    break;
                case 0:
                    System.out.println("Sortint...");
                    break;
                default:
                    break;
            }
        }while(ex != 0);
        
        sc.close();
    }

    public String getHash(String missatge) {
        try {
            // Convertim el text a un array de bytes utilitzant UTF-8
            byte[] bytesIn = missatge.getBytes(StandardCharsets.UTF_8);

            // Creem l'objecte per calcular el hash SHA-512
            MessageDigest sha512 = MessageDigest.getInstance("SHA-512");

            // Calculem el hash
            byte[] hashResult = sha512.digest(bytesIn);

            // Convertim el resultat a hexadecimal i el mostrem per pantalla
            StringBuilder textOut = new StringBuilder();
            for (int i = 0; i < hashResult.length; i++) {
                if (i > 0) {
                    textOut.append("-");
                }
                textOut.append(String.format("%02X", hashResult[i] & 0xFF));
            }

            return (textOut.toString());
        } catch (NoSuchAlgorithmException e) {
            System.out.println("No s'ha trobat l'algoritme SHA-512: " + e.getMessage());
            return ("No s'ha trobat l'algoritme SHA-512: " + e.getMessage());
        }
    }

    public void checkMissatge(){
        try {
            // 1. L'emissor escriu el missatge
            System.out.print("Introdueix el missatge original: ");
            String missatge = sc.nextLine();

            // 2. Guardem el missatge original al fitxer
            Files.writeString(
                Path.of("missatge.txt"),
                missatge,
                StandardCharsets.UTF_8
            );

            // 3. Calculem i guardem el hash original
            String hashOriginal = getHash(missatge);

            Files.writeString(
                Path.of("missatge.hash"),
                hashOriginal,
                StandardCharsets.UTF_8
            );

            System.out.println("Missatge i hash guardats correctament.");
            System.out.println("Hash original: " + hashOriginal);

            // 4. Simulem la recepció del missatge
            System.out.print("Introdueix el nom del fitxer rebut: ");
            String nomFitxer = sc.nextLine();

            // Limitem els noms de fitxer permesos
            if (!nomFitxer.equals("missatge.txt")
                    && !nomFitxer.equals("missatgeCanviat.txt")) {
                System.out.println("Nom de fitxer no vàlid.");
                return;
            }

            Path fitxerRebut = Path.of(nomFitxer);

            if (!Files.exists(fitxerRebut)) {
                System.out.println("El fitxer no existeix.");
                return;
            }

            // 5. Llegim el missatge rebut i el hash original
            String missatgeRebut = Files.readString(
                fitxerRebut, StandardCharsets.UTF_8
            );

            String hashDesat = Files.readString(
                Path.of("missatge.hash"), StandardCharsets.UTF_8
            );

            String hashRebut = getHash(missatgeRebut);

            System.out.println("\nHash original: " + hashDesat);
            System.out.println("Hash rebut:    " + hashRebut);

            // 6. Comparem els hash
            if (hashDesat.equals(hashRebut)) {
                System.out.println(
                    "El hash coincideix."
                );
            } else {
                System.out.println(
                    "Els hash són diferents."
                );
            }

        } catch (IOException e) {
            System.out.println("Error en gestionar els fitxers: "
                    + e.getMessage());
        } catch (Exception e) {
            System.out.println("No s'ha trobat SHA-256: "
                    + e.getMessage());
        }
    }

    public void registrarUsuari() {
    System.out.print("Nom d'usuari: ");
    String nom = sc.nextLine();

    if (users.contains(nom)) {
        System.out.println("Aquest nom d'usuari ja existeix.");
        return;
    }

    System.out.print("Contrasenya: ");
    String contrasenya = sc.nextLine();

    try {
        // Generem una salt aleatòria de 16 bytes
        byte[] bytesSalt = new byte[16];
        random.nextBytes(bytesSalt);

        // Convertim la salt a text hexadecimal
        String salt = java.util.HexFormat.of().formatHex(bytesSalt);

        // Calculem el hash de la contrasenya amb la salt
        String hash = getHash(contrasenya + salt);

        // Guardem només el nom, la salt i el hash
        users.add(nom);
        passwords.add(hash);
        salts.add(salt);

        System.out.println("Usuari registrat correctament.");

    } catch (Exception e) {
        System.out.println("Error en calcular el hash: "
                + e.getMessage());
    }
}

    public void iniciSessio() {

        System.out.println("Introdueix usuari:");
        String usuari = sc.nextLine();

        System.out.println("Introdueix contrasenya:");
        String password = sc.nextLine();

        int i = users.indexOf(usuari);

        if (i == -1){
            System.out.println("Credencials incorrectes");
        }else {
            String hashPassword = getHash(password + salts.get(i));
            if(passwords.get(i).equals(hashPassword)){
                System.out.println("Sessió iniciada correctament.");
            }else {
                System.out.println("Credencials incorrectes");
            }
        }
    }

}