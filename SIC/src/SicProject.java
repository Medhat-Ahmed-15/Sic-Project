import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.text.html.HTMLDocument.Iterator;

public class SicProject {
	
	static ArrayList <Integer> locationAddress=new ArrayList<Integer>();
    
    static HashMap<String, Integer>opTab = new HashMap<>();
    static HashMap<String, Integer>symTab = new HashMap<>();
    
    
    static int locCtr = 0;
    static String startAddr = "";
    static int progLen = 0;
  
    
    
    public static void main(String[] args) {
        
        opTab.put("ADD", Integer.parseInt("18", 16));
        opTab.put("AND", Integer.parseInt("40", 16));
        opTab.put("COMP", Integer.parseInt("28", 16));
        opTab.put("DIV", Integer.parseInt("24", 16));
        opTab.put("J", Integer.parseInt("3C", 16));
        opTab.put("JEQ", Integer.parseInt("30", 16));
        opTab.put("JGT", Integer.parseInt("34", 16));
        opTab.put("JLT", Integer.parseInt("38", 16));
        opTab.put("JSUB", Integer.parseInt("48", 16));
        opTab.put("LDA", Integer.parseInt("00", 16));
        opTab.put("LDCH", Integer.parseInt("50", 16));
        opTab.put("LDL", Integer.parseInt("08", 16));
        opTab.put("LDX", Integer.parseInt("04", 16));
        opTab.put("MUL", Integer.parseInt("20", 16));
        opTab.put("OR", Integer.parseInt("44", 16));
        opTab.put("RD", Integer.parseInt("D8", 16));
        opTab.put("RSUB", Integer.parseInt("4C", 16));
        opTab.put("STA", Integer.parseInt("0C", 16));
        opTab.put("STCH", Integer.parseInt("54", 16));
        opTab.put("STL", Integer.parseInt("14", 16));
        opTab.put("STSW", Integer.parseInt("E8", 16));
        opTab.put("STX", Integer.parseInt("10", 16));
        opTab.put("SUB", Integer.parseInt("1C", 16));
        opTab.put("TD", Integer.parseInt("E0", 16));
        opTab.put("TIX", Integer.parseInt("2C", 16));
        opTab.put("WD", Integer.parseInt("DC", 16));
        System.out.println(opTab);
        
        //PASS_1 
        File inputFile = new File("inputFile.txt");
        File symTabFile = new File("symTabFile.txt");
        File HTErec = new File("HTErecord.txt");
        try {
            Scanner scan_PASS1 = new Scanner(inputFile);
            PrintWriter writer_SYMTAB = new PrintWriter(symTabFile);
            Scanner scan_PASS2 = new Scanner(inputFile);
            PrintWriter writer_HTE = new PrintWriter(HTErec);
            
            int lineIndex = 0;
            
            while(scan_PASS1.hasNext()) {
                String line = scan_PASS1.nextLine();
                System.out.println(String.format("%04X", locCtr) + " " +line);
                String[] codeWords = line.split(" ");
                locationAddress.add(locCtr);
                
              
                
                if (codeWords.length == 3) {
                    if (codeWords[1].equals("START")) {
                        startAddr = codeWords[2];
                        locCtr = Integer.parseInt(startAddr, 16);//dah bayhawlo mn hexa la decimal
                    } else {
                        if (symTab.containsKey(codeWords[0])) {//ba na check law mafeesh haga duplicated mn 2asamee el instructions
                            System.out.println("Error, Duplicated Symbol");
                        } else {
                            symTab.put(codeWords[0], locCtr);
                            writer_SYMTAB.println(codeWords[0] + " " + String.format("%04X", Integer.parseInt(Integer.toHexString(locCtr), 16)));
                        }
                        if(opTab.containsKey(codeWords[1])) {//first if if there is no any special cases increment the location counter by 3 same as the index
                            locCtr += 3;
                            lineIndex++;
                        } else if(codeWords[1].equals("WORD")) {//first case if the instruction is "Word"
                            if(codeWords[2].contains(",")) {//dah law kaza rakam ex:100,3,50
                                String[] arrayWords = codeWords[2].split(",");
                                for(int i = 0; i < arrayWords.length; i++) {
                                    locCtr += 3;
                                    lineIndex++;
                                }
                            } else {
                                    locCtr += 3;
                                    lineIndex++;
                            }
                        } else if(codeWords[1].equals("RESW")) {//second case if the instruction is"RESW"
                            locCtr += (3 * Integer.parseInt(codeWords[2]));
                            lineIndex++;
                        } else if(codeWords[1].equals("RESB")) {//third case if the instruction is"RESB"
                            locCtr += Integer.parseInt(codeWords[2]);
                            lineIndex++;
                        } else if(codeWords[1].equals("BYTE")) {//fourth case if the instruction is"BYTE"
                        
                        
                        	 if(codeWords[2].contains("C")) {
                                 codeWords[2] = codeWords[2].substring(2, codeWords[2].length()-1);
                                 locCtr += codeWords[2].length();
                             } else if (codeWords[2].contains("X")) {
                                 codeWords[2] = codeWords[2].substring(2, codeWords[2].length()-1);
                                 double numBytes = Math.ceil(codeWords[2].length() / 2);
                                 locCtr += numBytes;
                             } else {
                                 locCtr++;
                             }
                           
                        }
                        	
                    }
                } else if(codeWords.length == 2) {
                    if(opTab.containsKey(codeWords[0])) {
                        locCtr += 3;
                        lineIndex++;
                    } else if(opTab.containsKey(codeWords[1])) {
                        if(symTab.containsKey(codeWords[0])) {
                            System.out.println("Duplication Error");
                        } else {
                            symTab.put(codeWords[0], locCtr);
                            writer_SYMTAB.println(codeWords[0] + " " + String.format("%04X", Integer.parseInt(Integer.toHexString(locCtr), 16)));
                            locCtr += 3;
                            lineIndex++;
                        }
                    } 
                    else if(codeWords[0].equals("END")) {
                        break;
                    } 
                } else if(codeWords.length == 1) {
                    locCtr += 3;
                    lineIndex++;
                }
                
            }
            scan_PASS1.close();
            writer_SYMTAB.close();
            System.out.println("Starting locCtr: " + Integer.parseInt(startAddr, 16) + ", In Hex: " + startAddr);
            System.out.println("Final locCtr: " + locCtr + ", In Hex: " + String.format("%04X", Integer.parseInt(Integer.toHexString(locCtr), 16)));
            progLen = locCtr - Integer.parseInt(startAddr, 16);
            System.out.println("Program Length: " + progLen + ", In Hex: " + String.format("%04X", Integer.parseInt(Integer.toHexString(progLen), 16)));
            
            //PASS_2 
            String tRec = "";//variable to store the object in it
            int lineIndex_PASS2 = 0;
            int tStartAddr = 0;  
            
            while(scan_PASS2.hasNext()){
                String line = scan_PASS2.nextLine();
                String[] codeWords = line.split(" ");
                String objectCode = "";
                if(codeWords.length == 3) {
                    if(codeWords[1].equals("START")) {
                        String progName = codeWords[0];//return the name of the program
                        if(progName.length() > 6) {
                            progName = progName.substring(0, 6);//hna ba2asm el string baheis yab2 equal 6 digits bs
                        } else if(progName.length() < 6) {
                            for(int i = 0; progName.length() < 6; i++) {//hna ba2 3ashan howa 2as8r mn 6 fana bazawd ba spaces 3ashan 2akml 6 using the loop
                            progName = progName + "_";
                            }
                        }
                        writer_HTE.println("H " + progName + String.format("%06X", Integer.parseInt(startAddr, 16)) + " " + String.format("%06X", progLen));
                        tStartAddr = Integer.parseInt(startAddr, 16);
                    } else {
                        if (opTab.containsKey(codeWords[1])) {
                            objectCode = String.format("%02X", opTab.get(codeWords[1]));
                            objectCode += String.format("%04X", symTab.get(codeWords[2]));
                            
                        } else if (codeWords[1].equals("WORD")) {
                            objectCode = String.format("%06X", Integer.parseInt(codeWords[2]));
                        } else if (codeWords[1].equals("BYTE")) {
                            if(codeWords[2].contains("C")) {
                                codeWords[2] = codeWords[2].substring(2, codeWords[2].length()-1);
                                for(char c : codeWords[2].toCharArray()) {
                                    objectCode += String.format("%02X", (int) c);//(int) c this used to bring the ascii code
                                }
                            } else if (codeWords[2].contains("X")) {
                                codeWords[2] = codeWords[2].substring(2, codeWords[2].length()-1);
                                objectCode = String.format("%02X", Integer.parseInt(codeWords[2], 16));
                            } else {
                                objectCode = String.format("%02X", Integer.parseInt(codeWords[2]));
                            }
                        } 
                    }
                } else if(codeWords.length == 2) {
                    if(opTab.containsKey(codeWords[0])) {
                        objectCode = String.format("%02X", opTab.get(codeWords[0]));
                        int opCode = 0x0000;//this for the x if it  is equal 0 
                        
                        
                        if(codeWords[1].contains(",")){
                            opCode = 0x8000;//this for the x if it  is equal 1
                            codeWords[1] = codeWords[1].split(",")[0];//return the value that is in the given index 
                        }
                        if(symTab.containsKey(codeWords[1])) {
                            opCode += symTab.get(codeWords[1]);
                        }
                        objectCode += String.format("%04X", opCode);
                    }
                } else if(codeWords.length == 1) {
                    objectCode = String.format("%02X", opTab.get(codeWords[0]));
                    objectCode += "0000";//law wahd yab2 mafeesh address yab2 then ba zero
                } if(((tRec + " " + objectCode).replace(" ", "").length() > 60) || (codeWords[1].equals("RESB")) || (codeWords[1].equals("RESW"))) {
                    int tLen = tRec.replace(" ", "").length();
                    tLen /= 2;
                    writer_HTE.println("T " + String.format("%06X", tStartAddr) + " " + String.format("%02X", tLen) + tRec);
                    tRec = objectCode;
                    tStartAddr = locationAddress.get(lineIndex_PASS2);
                } else {
                    tRec += " " + objectCode;
                }
                lineIndex_PASS2++;
            }
            int tLen = tRec.replace(" ", "").length();
            tLen /= 2;
            writer_HTE.println("T " + String.format("%06X", tStartAddr) + " " + String.format("%02X", tLen) + tRec);
            writer_HTE.print("E " + String.format("%06X", Integer.parseInt(startAddr, 16)));
            writer_HTE.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SicProject.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

}