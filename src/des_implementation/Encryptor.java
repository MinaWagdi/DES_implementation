/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package des_implementation;

import java.math.BigInteger;

/**
 *
 * @author minarafla
 */
public class Encryptor {

    private final int[][] PC1;
    private final int[][] PC2;
    private final int[][] IP_table;
    private final int[][] E_selectionTable;
    private final int[][] PermutationTable;
    private final int[][] InversePermutationTable;
    String key;
    private String key_afterPermutation;
    String message;
    private final int[] left_shiftsCountArray;
    private String[] C;
    private String[] D;
    private String[] SubKeysArray_16keys;
    private String[] L;//of the permuted messages
    private String[] R;//of the permuted messages
    private String IP = "";//the message permuted in the initial permuation phase
    String cypherText="";

    public Encryptor(String key, String message) {
        this.key = key;
        this.message = message;
        this.key_afterPermutation = "";

        PC1 = new int[][]{
            {57, 49, 41, 33, 25, 17, 9},
            {1, 58, 50, 42, 34, 26, 18},
            {10, 2, 59, 51, 43, 35, 27},
            {19, 11, 3, 60, 52, 44, 36},
            {63, 55, 47, 39, 31, 23, 15},
            {7, 62, 54, 46, 38, 30, 22},
            {14, 6, 61, 53, 45, 37, 29},
            {21, 13, 5, 28, 20, 12, 4}
        };
        left_shiftsCountArray = new int[]{1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1};
        PC2 = new int[][]{
            {14, 17, 11, 24, 1, 5},
            {3, 28, 15, 6, 21, 10},
            {23, 19, 12, 4, 26, 8},
            {16, 7, 27, 20, 13, 2},
            {41, 52, 31, 37, 47, 55},
            {30, 40, 51, 45, 33, 48},
            {44, 49, 39, 56, 34, 53},
            {46, 42, 50, 36, 29, 32}
        };
        IP_table = new int[][]{
            {58, 50, 42, 34, 26, 18, 10, 2},
            {60, 52, 44, 36, 28, 20, 12, 4},
            {62, 54, 46, 38, 30, 22, 14, 6},
            {64, 56, 48, 40, 32, 24, 16, 8},
            {57, 49, 41, 33, 25, 17, 9, 1},
            {59, 51, 43, 35, 27, 19, 11, 3},
            {61, 53, 45, 37, 29, 21, 13, 5},
            {63, 55, 47, 39, 31, 23, 15, 7}
        };

        E_selectionTable = new int[][]{
            {32, 1, 2, 3, 4, 5},
            {4, 5, 6, 7, 8, 9},
            {8, 9, 10, 11, 12, 13},
            {12, 13, 14, 15, 16, 17},
            {16, 17, 18, 19, 20, 21},
            {20, 21, 22, 23, 24, 25},
            {24, 25, 26, 27, 28, 29},
            {28, 29, 30, 31, 32, 1}

        };

        PermutationTable = new int[][]{
            {16, 7, 20, 21},
            {29, 12, 28, 17},
            {1, 15, 23, 26},
            {5, 18, 31, 10},
            {2, 8, 24, 14},
            {32, 27, 3, 9},
            {19, 13, 30, 6},
            {22, 11, 4, 25}

        };
        InversePermutationTable = new int[][]{
            {40, 8, 48, 16, 56, 24, 64, 32},
            {39, 7, 47, 15, 55, 23, 63, 31},
            {38, 6, 46, 14, 54, 22, 62, 30},
            {37, 5, 45, 13, 53, 21, 61, 29},
            {36, 4, 44, 12, 52, 20, 60, 28},
            {35, 3, 43, 11, 51, 19, 59, 27},
            {34, 2, 42, 10, 50, 18, 58, 26},
            {33, 1, 41, 9, 49, 17, 57, 25}

        };
    }

    public String permute_using_PC1(String key) {
        String newKey = "";
        for (int i = 0; i < PC1.length; i++) {
            for (int j = 0; j < PC1[1].length; j++) {
                int index = PC1[i][j];
                index--;
                char originalBit = getBit(key, index);
                newKey = appendBinaryString(newKey, originalBit);
            }
        }
        return newKey;
    }

    public void doLeftShiftsOperations(String keyPermuted) {
        C = new String[17];
        D = new String[17];
        int mid = keyPermuted.length() / 2;
        String C0 = keyPermuted.substring(0, mid);
        String D0 = keyPermuted.substring(mid, keyPermuted.length());
        C[0] = C0;
        D[0] = D0;
        if (C0.length() != D0.length()) {
            System.out.println("ERROR Number of bits in C0 and C1 are not equal");
            System.out.println(C[0].length());
            System.out.println(D[0].length());
        }
        for (int i = 1; i < 17; i++) {
            int numberOfShifts = left_shiftsCountArray[i - 1];
            C[i] = do_LeftShifts(C[i - 1], numberOfShifts);
            D[i] = do_LeftShifts(D[i - 1], numberOfShifts);
        }
    }

    public String[] permute_using_PC2() {
        String[] newKey = new String[17];
        newKey[0] = C[0] + "" + D[0];
        for (int m = 1; m < newKey.length; m++) {
            newKey[m] = "";
            String k = C[m] + "" + D[m];
            for (int i = 0; i < PC2.length; i++) {
                for (int j = 0; j < PC2[1].length; j++) {
                    int index = PC2[i][j];
                    index--;
                    char originalBit = getBit(k, index);
                    newKey[m] = appendBinaryString(newKey[m], originalBit);
                }
            }
        }
        return newKey;
    }

    public String[] keyGenerator(String key) {
        key_afterPermutation = permute_using_PC1(key);
        doLeftShiftsOperations(key_afterPermutation);
        SubKeysArray_16keys = permute_using_PC2();
        return SubKeysArray_16keys;

    }

    public String encrypt() {
        //step 1 key generator and permutations
        //note that k[0] is not important, it's just the permutation of C[0] and D[0]
        SubKeysArray_16keys = keyGenerator(key);
        
        //Step 2: Encode each 64-bit block of data.
        IP = doInitialPermuation(this.message);
        cypherText=doRounds(16, IP);
        return cypherText;
    }

    public String doInitialPermuation(String m) {
        String messagePermuted = "";
        for (int i = 0; i < IP_table.length; i++) {
            for (int j = 0; j < IP_table[1].length; j++) {
                int index = IP_table[i][j];
                index--;
                char originalBit = getBit(m, index);
                messagePermuted = appendBinaryString(messagePermuted, originalBit);
            }
        }
        return messagePermuted;

    }

    private String doRounds(int rounds_count, String messagePermuted) {
        int mid = messagePermuted.length() / 2;
        String L0 = messagePermuted.substring(0, mid);
        String R0 = messagePermuted.substring(mid, messagePermuted.length());
        L = new String[rounds_count + 1];
        R = new String[rounds_count + 1];
        L[0] = "";
        R[0] = "";
        L[0] = L0;
        R[0] = R0;
        for (int i = 1; i <= rounds_count; i++) {
            L[i] = R[i - 1];
            R[i] = XOR_BinaryStrings(L[i - 1], FunctionOfEachRound(R[i - 1], SubKeysArray_16keys[i]));
        }
        String OUTPUT_ROUNDS_COUNT = R[rounds_count] + L[rounds_count];
//        System.out.println("OUTPUT_ROUNDS_COUNT "+OUTPUT_ROUNDS_COUNT);
        

        /*INVERSE PERMUTATION*/
        String OUTPUT_AFTER_INVERSE_PERMUTATION = "";
        OUTPUT_AFTER_INVERSE_PERMUTATION = permute_usingInversePermutationTable(OUTPUT_ROUNDS_COUNT);
        
        BigInteger bigInt = new BigInteger(OUTPUT_AFTER_INVERSE_PERMUTATION,2);
        String hex = bigInt.toString(16);
        
        
        
        return hex;
    }

    public String FunctionOfEachRound(String r, String k) {
        String OUTPUT_FROM_SBOX = "";
        String expandedR_Prev = expandBlockUsingE_SelectionTable(r);
        String XOR_result = XOR_BinaryStrings(expandedR_Prev, k);

        // le 7ad eli fat da el mafroom eno mazboot
        String B1 = XOR_result.substring(0, 6);
        String B2 = XOR_result.substring(6, 12);
        String B3 = XOR_result.substring(12, 18);
        String B4 = XOR_result.substring(18, 24);
        String B5 = XOR_result.substring(24, 30);
        String B6 = XOR_result.substring(30, 36);
        String B7 = XOR_result.substring(36, 42);
        String B8 = XOR_result.substring(42, 48);

        SBox s = new SBox();
        OUTPUT_FROM_SBOX += s.getValueFromSBox(B1, "1");
        OUTPUT_FROM_SBOX += s.getValueFromSBox(B2, "2");
        OUTPUT_FROM_SBOX += s.getValueFromSBox(B3, "3");
        OUTPUT_FROM_SBOX += s.getValueFromSBox(B4, "4");
        OUTPUT_FROM_SBOX += s.getValueFromSBox(B5, "5");
        OUTPUT_FROM_SBOX += s.getValueFromSBox(B6, "6");
        OUTPUT_FROM_SBOX += s.getValueFromSBox(B7, "7");
        OUTPUT_FROM_SBOX += s.getValueFromSBox(B8, "8");
        
        String SBOX_OUTPUT_AFTER_PERMUTATION = permute_usingPermutationTable(OUTPUT_FROM_SBOX);

        return SBOX_OUTPUT_AFTER_PERMUTATION;
    }

    public String permute_usingPermutationTable(String kk) {
        String newKey = "";
        
        
        for (int i = 0; i < PermutationTable.length; i++) {
            for (int j = 0; j < PermutationTable[1].length; j++) {
                int index = PermutationTable[i][j];
                index--;
                char originalBit = getBit(kk, index);
                newKey = appendBinaryString(newKey, originalBit);
            }
        }
        return newKey;
    }

    public String permute_usingInversePermutationTable(String kk) {
        String newKey = "";
        for (int i = 0; i < InversePermutationTable.length; i++) {
            for (int j = 0; j < InversePermutationTable[1].length; j++) {
                int index = InversePermutationTable[i][j];
                index--;
                char originalBit = getBit(kk, index);
                newKey = appendBinaryString(newKey, originalBit);
            }
        }
        return newKey;
    }

    private char getBit(String n, int k) {
        return n.charAt(k);

    }

    private String appendBinaryString(String i, char j) {
        return i + j;
    }

    private String do_LeftShifts(String s, int shift_count) {
        //shift_count = shift_count % s.length();
        return s.substring(shift_count) + s.substring(0, shift_count);
    }

    public String XOR_BinaryStrings(String a, String b) {
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

    //this method has an output of 48 bits
    public String expandBlockUsingE_SelectionTable(String inputBlock_32Bits) {
        String output_48bits = "";
        for (int i = 0; i < E_selectionTable.length; i++) {
            for (int j = 0; j < E_selectionTable[1].length; j++) {
                int index = E_selectionTable[i][j];
                index--;
                char originalBit = getBit(inputBlock_32Bits, index);
                output_48bits = appendBinaryString(output_48bits, originalBit);
            }
        }
        return output_48bits;
    }

}
