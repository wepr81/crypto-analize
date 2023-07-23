import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class CryptoAnalyzer {
    private static ArrayList<Character> alphabet =   new ArrayList(Arrays.asList('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
            'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            ',', '.', '"', ' ', ':', '-', '!', '?', '(', ')'));
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String fileName;
        String choice;
        int key = 0;
        System.out.println("Choose your action:");
        System.out.println("if you want to encrypt text enter - 1,");
        System.out.println("if you want to decrypt text enter - 2,");
        System.out.println("if you want to decrypt text by brute force enter - 3,");
        int action;
        do {
            action = getKey("enter 1 or 2 or 3", scanner);
        }
        while(action < 1 || action > 3) ;
        if(action == 1) {
            choice = "encryption";
        } else {
            choice = "decryption";
        }
        fileName = getFileName("Enter filename to " + choice, scanner);
        if(action != 3){
            key = getKey("Enter key", scanner);
        }

       switch (action){
           case 1: encrypt(fileName, key);
                    break;
           case 2: decrypt(fileName, key);
                    break;
           case 3: bruteForce(fileName);
       }
    }
    public static void encrypt(String fileName, int key){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] source = readFileToByteArray(fileName);
        for (byte symbol: source ){
           if(symbol == 13) {// заміна переносу на пробіл
               symbol = 32;
           }
           Character castChar = Character.toLowerCase((char)symbol);

           if(alphabet.contains(castChar)) {
               int index = alphabet.indexOf(castChar);
               int encrypted = (index + key) % alphabet.size();
               if(encrypted < 0){
                   encrypted = alphabet.size() + encrypted;
               }
               outputStream.write(alphabet.get(encrypted));
           }
        }
        Path encryptedFile = Path.of(getNewFileName(fileName, "encrypted"));
        writeToFile(encryptedFile, outputStream);
    }
    public static void decrypt(String fileName, int key){
        byte[] source = readFileToByteArray(fileName);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        for (byte symbol: source ){
            int decrypted = (alphabet.indexOf((char)symbol) - key)% alphabet.size();
            if(decrypted < 0) {
                decrypted = alphabet.size() + decrypted;
            }
            outputStream.write(alphabet.get(decrypted));
        }
        Path decryptedFile = Path.of(getNewFileName(fileName, "decrypted"));
        writeToFile(decryptedFile, outputStream);
    }
    public static void bruteForce(String fileName){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] source = readFileToByteArray(fileName);
        char[] temporary = new char[source.length];
        boolean isComaSpaceTogether;

        for(int key = 1; key < alphabet.size(); key++){
            int index = 0;
            int comaCount =0;
            int spaceCount =0;
            isComaSpaceTogether = false;
            for (byte symbol: source ){
                int decrypted = (alphabet.indexOf((char)symbol) - key)% alphabet.size();
                if(decrypted < 0) {
                    decrypted = alphabet.size() + decrypted;
                }
                temporary[index] = alphabet.get(decrypted);
                if(temporary[index] == ','){// підраховуємо кількість ком
                    comaCount++;
                }
                if(temporary[index] == ' '){// підраховуємо кількість пробілів
                    spaceCount++;
                }
                if(index > 0 && temporary[index] == ' ' && temporary[index-1] == ','){//пошук послідовності кома пробіл
                    isComaSpaceTogether = true;
                }
                index++;
            }
            //умова співпадіння, коли є послідовність кома пробіл, пробілів більше ком і середня довжина слова меньше 8
            if(isComaSpaceTogether && comaCount <= spaceCount && ((temporary.length - spaceCount)/(spaceCount+1)) < 8){
                break;
            }
            if(temporary[temporary.length-1] == '.' && comaCount <= spaceCount){//перевірка на крапку в кінці тексту
                break;
            }
        }
        for (int i = 0; i < source.length; i++){
            outputStream.write((byte)temporary[i]);
        }
        Path decryptedFile = Path.of(getNewFileName(fileName, "decrypted"));
        writeToFile(decryptedFile, outputStream);
    }
    private static String getFileName(String message, Scanner scanner){
        System.out.println(message);
        boolean isFileExist = false;
        String fileName;
        Path path;
        do {
            fileName = scanner.nextLine();
            try {
                path = Path.of(fileName);
            } catch (Exception e){
                System.out.println("wrong filename, please enter valid filename or 'exit' to stop");
                continue;
            }
            if(fileName.equals("exit")) {
               System. exit(0);
            }
            if(Files.notExists(path)) {
                System.out.println("File is not exist, please enter valid filename or 'exit' to stop");
            } else {
                isFileExist = true;
            }
            if(!Files.isRegularFile(path)){
                System.out.println("It is not a File, please enter valid filename or 'exit' to stop");
                isFileExist = false;
            }

        }
        while(!isFileExist);
        return fileName;
    }
    private static int getKey(String message, Scanner scanner){
        System.out.println(message);
        boolean validEnter ;
        int key = 0;
        do{
            try{
                validEnter = true;
                key = Integer.parseInt(scanner.nextLine());
            } catch (Exception e){
                System.out.println("Wrong, you need to enter digit");
                validEnter = false;
            }
        } while (!validEnter);
        return key;
    }
    private static byte[] readFileToByteArray(String fileName){
        byte[] source = new byte[0];
        try{
            source = Files.readAllBytes(Path.of(fileName));
        } catch (Exception e) {
            System.out.println("Error read file");
        }
        return source;
    }
    private static String getNewFileName(String fileName, String suffix){
        int dotIndex = fileName.lastIndexOf(".");
        return fileName.substring(0, dotIndex) + suffix + fileName.substring(dotIndex);
    }
    private static void writeToFile(Path fileName, ByteArrayOutputStream outputStream){
        try {
            Files.write(fileName, outputStream.toByteArray());
        } catch (IOException e){
            System.out.println("something get wrong");
        }
    }
}
