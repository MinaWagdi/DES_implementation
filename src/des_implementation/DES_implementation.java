/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package des_implementation;

import java.math.BigInteger;
import java.util.Scanner;

/**
 *
 * @author minarafla
 */
public class DES_implementation {

    /**
     * @param args the command line arguments
     */
    public static String XOR_BinaryStrings(String a, String b) {
        String result = "";
        if (a.length() != b.length()) {
            System.out.println("ERRRROOORRRR!!!!!Trying to XOR 2 binary numbers that don't have the same number of bits!!!!!");
        }
        for (int i = 0; i < a.length(); i++) {
            char x = a.charAt(i);
            char y = b.charAt(i);
            char z;
            if (x != y) {
                z = '1';
            } else {
                z = '0';
            }
            result += z;
        }

        return result;
    }

    public static void main(String[] args) {
        Scanner reader = new Scanner(System.in);  // Reading from System.in
        System.out.println("Enter a 16 bit key in hexadecimal, without spaces (make sure they are 16 digits): ");
        String hexaStringKEY = reader.next();
        System.out.println("Enter a 16 bit message in hexadecimal, without spaces (make sure they are 16 digits): ");
        String hexaStringMessage = reader.next();
        System.out.println("Enter number of rounds in decimal, without spaces: ");
        int rounds = reader.nextInt();
        
        String BinaryStringKEY = hexToBinary(hexaStringKEY);
        String BinaryStringMessage = hexToBinary(hexaStringMessage);
        
//        Encryptor e = new Encryptor(BinaryStringKEY, BinaryStringMessage);
//
//        System.out.println(e.encrypt());
//        
        Encryptor e1 = new Encryptor(BinaryStringKEY, BinaryStringMessage);
        String Hex_cypher="";
        String Bin_cypher="";
        for(int i =0;i<rounds;i++){
            Hex_cypher = e1.encrypt();
            Bin_cypher = hexToBinary(Hex_cypher);
            e1.message=Bin_cypher;
        }
        System.out.println("The cypher text is "+Hex_cypher);
    }

    public static String hexToBinary(String hex) {
        BigInteger bigInt = new BigInteger(hex, 16);
        String bin = bigInt.toString(2);
        while (bin.length() < 64) {
            bin = '0' + bin;
        }
        return bin;
    }

}
