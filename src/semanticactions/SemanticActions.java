/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


package semanticactions;

import errors.*;
import java.util.*;
import parser.*;
import symboltable.*;
import tokenizer.*;

public class SemanticActions {
	
	private Stack<Object> semanticStack ;
	private Quadruples quads ;
	private boolean insert ;
	private boolean isArray ;
	private boolean global ;
        private boolean isParm;
        private boolean debug;
        SymbolTable globalTable, constantTable;
        SymbolTable localTable;
	private int globalMemory, globalStore ;
	private int localMemory ;
        private int localStore;
        private int tableSize;
        private SymbolTableEntry currentFunction;
        private Stack<Integer> parms;
        private Stack<Stack<SymbolTableEntry>> nextParms;
        private int parmInfoCount;
        private ArrayList<String> errors;

	public SemanticActions() {
		semanticStack = new Stack<Object>();
                parms = new Stack<Integer>();
		quads = new Quadruples();
                tableSize = 37;
		insert = true;
		isArray = false;
		isParm = false;
		global = true;
                debug = true;
		globalMemory = 0 ;
                globalStore = 0;
                localStore = 0;
		localMemory = 0;
                parmInfoCount = 0;
                this.nextParms = new Stack<Stack<SymbolTableEntry>>();
                this.currentFunction = new NullEntry("null");
		globalTable = new SymbolTable(tableSize);
		constantTable = new SymbolTable(tableSize);
                globalTable.installBuiltins();
                errors = new ArrayList<String>();
	}
        
        //Print the contents of the semantic stack
        public void dumpStack(){
            for (Object obj: this.semanticStack){
                if (obj instanceof Token){
                Token token = (Token) obj;
                System.out.println("Token: " + token.getType() + ", Value: " + token.getValue());
                }
                else{
                    if (obj instanceof Etype){
                        System.out.println("ETYPE: " + obj.toString());
                        System.out.println();
                    }
                    else if (obj instanceof int[]){
                        System.out.println(Arrays.toString((int[])obj));
                    }
                    else if (obj instanceof Integer){
                        System.out.println(Integer.toString((int)obj));
                    }
                    else{
                    SymbolTableEntry entry = (SymbolTableEntry) obj;
                    entry.print();
                    }
                }
            }
        }
        
        public void dumpGlobalTable(){
            this.globalTable.dumpTable();
        }
        public void dumpConstantTable(){
            this.constantTable.dumpTable();
        }
        public SymbolTable getGlobalTable(){
            return this.globalTable;
        }
        public SymbolTable getConstantTable(){
            return this.constantTable;
        }
        public Quadruples getQuads(){
            return this.quads;
        }
        
        public String makeRealOperand(SymbolTableEntry realEntry){
            String returnString = null;
                if (realEntry.getName().contains("e")){
                    String[] numbers = realEntry.getName().split("e");
                    returnString = numbers[0];
                    String exp = numbers[1];
                    int exponent = Integer.parseInt(exp);
                    for (int i = 0; i < exponent; i++){
                        returnString = returnString + 0;
                    }
                }
                else if (realEntry.getName().contains("E")){
                    String[] numbers = realEntry.getName().split("E");
                    returnString = numbers[0];
                    String exp = numbers[2];
                    int exponent = Integer.parseInt(exp);
                    for (int i = 0; i < exponent; i++){
                        returnString = returnString + 0;
                    }
                }
                else{
                    returnString = realEntry.getName();
                }
            return returnString;
        }
        
        
        
        
        /*
         * Lookup key in appropriate symbolTable and return entry.
         */
        public SymbolTableEntry lookup(String key){
            if (global){
                return this.globalTable.lookup(key);
            }
            else{
                if (!(this.localTable.lookup(key) == null)){
                return this.localTable.lookup(key);
                }
                else{
                    return this.globalTable.lookup(key);
                }
            }
        }
        
        public SymbolTable getLocalTable(){
            return this.localTable;
        }
        
        /*
        Put constants in actual memory address (To be used before generating any quadruple)
        */
        public SymbolTableEntry generateConstant(SymbolTableEntry id, int index){
            if (id instanceof ConstantEntry){
                SymbolTableEntry tempEntry = create("TEMP" + index, id.getType());
                String operand = makeRealOperand(id);
                generate("move", operand, tempEntry);
            return tempEntry;
            }
            else {
                return id;
            }
        }
        
        public void setLocal(){
            this.global = false;
        }
        
        /*
         * Return string for address representation of SymbolTableEntry operand.
         */
        public String makeOperand(SymbolTableEntry operand){
            String returnString = null;
            if (global){
                if (operand.hasAddress()){
                    int address = Math.abs(operand.getAddress());
                     returnString = "_" + address;
                }
                else{
                     returnString = operand.getName();
                }
            }
            else{
                if (!(this.localTable.lookup(operand.getName()) == null)){
                    if (operand.hasAddress()){
                        int address = Math.abs(operand.getAddress());
                         returnString = "%" + address;
                    }
                    else{
                         returnString = operand.getName();
                    }
                }
                else{
                    if (operand.hasAddress()){
                        int address = Math.abs(operand.getAddress());
                        returnString = "_" + address;
                }
                else{
                     returnString = operand.getName();
                }
                      
                    
                }
            }
            if (operand.isParm()){
                return "^" + returnString;
            }
         
            else{
                 return returnString;
            }
        }
        
        
                /*
         * Return string address for operands that are parameters for gen calls.
         */
        public String makeParamOperand(SymbolTableEntry operand){
            if (global){
                return "@_" + operand.getAddress();
            }
            else{
                if (operand.isParm()){
                    return "%" + operand.getAddress();
                }
                else{
                    return "@%" + operand.getAddress();
                }
            }
        }
        
        
        /*
        // Insert symboltable entry into local table, first check global table to 
        // see if entry is marked as restricted.
        */
        public void insertLocal(SymbolTableEntry entry, SymbolTable localTable){
            // check if globalTable contains entry, if so, check if restricted,
            // only add to local table if not 
            if (this.globalTable.contains(entry)){
                if (!(this.globalTable.lookup(entry.getName()).isRestricted())){
                    localTable.insert(entry);
                }
            }
            else{
                localTable.insert(entry);
            }
        }
        
                /*
        Create new memory location.
        */
        public SymbolTableEntry create(String name, TokenType type){
            if (this.global){
                this.globalTable.insert(new VariableEntry("$$" + name, type, -this.globalMemory));
                this.globalMemory++;
            }
            else{
                this.localTable.insert(new VariableEntry("$$" + name, type, -this.localMemory));
                localMemory++;          
            }
            return this.lookup("$$" + name);
        }
        
        /*
        Check operand types for expressions.
        */
        public int typeCheck(TokenType type1, TokenType type2){
            if ((type1 == TokenType.INTEGER) && (type2 == TokenType.INTEGER)){
                return 0;
            }
            if ((type1 == TokenType.REAL) && (type2 == TokenType.REAL)){
                return 1;
            }
            if ((type1 == TokenType.REAL) && (type2 == TokenType.INTEGER)){
                return 2;
            }
            else if ((type1 == TokenType.INTEGER) && (type2 == TokenType.REAL)){
                return 3;
            }
            else {return -1;}
        }
        
        /*
        Create a new list containing only integer i and return list.
        */
        public int[] makeList(int i){
            int[] returnList = new int[1];
            returnList[0] = (i);
            return returnList;     
        }
        
        /*
        Concatenate lists 1 and 2 and return concatenated list.
        */
        public int[] merge(int[] list1, int[] list2){
            int[] mergeList = new int[list1.length + list2.length];
            for (int i = 0; i < list1.length; i++){
                mergeList[i] = list1[i];
            }
            for (int j = list1.length; j < (list1.length+list2.length); j++){
                mergeList[j] = list2[j- list1.length];
            }
            return mergeList;
        }
        
        /*
        Insert integer i as target label for all statements on the list
        */
        public void backpatch(int[] list, int i){
            for (int index: list){
                String[] quad = this.quads.getQuad(index);
                quad[quad.length-1] = Integer.toString(i);
            }
            
        }
        
        /*
        Generate TVI code quadruple with one field.
        */
        public void generate(String tviCode){
            String[] fields = new String[1];
            fields[0] = tviCode;
            this.quads.addQuad(fields);
            this.quads.incrementNextQuad(); 
        }
        
        /*
        Generate TVI code quadruple with 2 fields: String, String.
        */
        public void generate(String tviCode, String operand1){
            String[] fields = new String[2];
            fields[0] = tviCode;
            fields[1] = operand1;
            this.quads.addQuad(fields);
            this.quads.incrementNextQuad();
        }
        
   
       /*
        Generate TVIcode with 3 fields: String, String, SymbolTableEntry
        */
        public void generate(String tviCode, String operand1, SymbolTableEntry operand2){
            operand2 = generateConstant(operand2,0);
            String[] fields = new String[3];
            fields[0] = tviCode;
            fields[1] = operand1;
            fields[2] = makeOperand(operand2);
            this.quads.addQuad(fields);
            this.quads.incrementNextQuad();
        }
        
        

        /*
        Generate TVI code quadruple with 2 fields: String, SymbolTableEntry.
        */
        public void generate(String tviCode, SymbolTableEntry operand1){
            operand1 = generateConstant(operand1,0);
            String[] fields = new String[2];
            fields[0] = tviCode;
              if (!(tviCode == "param")){ 
                fields[1] = makeOperand(operand1);
              }
              else{
                      fields[1] =  makeParamOperand(operand1); 
              }
            this.quads.addQuad(fields);
            this.quads.incrementNextQuad();   
            
    
        }
        
        /*
        Generate TVI code quadruple with 3 fields: String, SymbolTableEntry, String.
        */
        public void generate(String tviCode, SymbolTableEntry operand1, String operand2){
            operand1 = generateConstant(operand1,0);
            String[] fields = new String[3];
            fields[0] = tviCode;
            fields[1] = makeOperand(operand1);
            fields[2] = operand2;
            this.quads.addQuad(fields);
            this.quads.incrementNextQuad();
        }
        
        /*
        Generate TVI code quadruple with 4 fields: String, SymbolTableEntry, SymbolTableEntry, SymbolTableEntry.
        */
        public void generate(String tviCode, SymbolTableEntry operand1, SymbolTableEntry operand2, SymbolTableEntry operand3){
            operand1 = generateConstant(operand1,0);
            operand2 = generateConstant(operand2,1);
            operand3 = generateConstant(operand3,2);
            String[] fields = new String[4];
            fields[0] = tviCode;
            fields[1] = makeOperand(operand1);
            fields[2] = makeOperand(operand2);
            fields[3] = makeOperand(operand3);
            this.quads.addQuad(fields);
            this.quads.incrementNextQuad();
        }
        
        /*
        Generate TVI code quadruple with 4 fields: String, SymbolTableEntry, SymbolTableEntry, String. 
        */
        public void generate(String tviCode, SymbolTableEntry operand1, SymbolTableEntry operand2, String operand3){
            operand1 = generateConstant(operand1,0);
            operand2 = generateConstant(operand2,1);
            String[] fields = new String[4];
            fields[0] = tviCode;
            fields[1] = makeOperand(operand1);
            fields[2] = makeOperand(operand2);
            fields[3] = operand3;
            this.quads.addQuad(fields);
            this.quads.incrementNextQuad();
        }
        
             /*
        Generate TVI code quadruple with 4 fields: String, SymbolTableEntry, String, SymbolTableEntry. 
        */
        public void generate(String tviCode, SymbolTableEntry operand1, String operand2, SymbolTableEntry operand3){
            operand1 = this.generateConstant(operand1,0);
            operand3 = this.generateConstant(operand3,1);
            String fields[] = new String[4];
            fields[0] = tviCode;
            fields[1] = makeOperand(operand1);
            fields[2] = operand2;
            fields[3] = makeOperand(operand3);
            this.quads.addQuad(fields);
            this.quads.incrementNextQuad();
            
        }
        
        /*
        Generate TVI code quadruple with 3 fields: String, SymbolTableEntry, SymbolTableEntry. 
        */
        public void generate(String tviCode, SymbolTableEntry operand1, SymbolTableEntry operand2){
            operand1 = generateConstant(operand1,0);
            operand2 = generateConstant(operand2,1);
            String[] fields = new String[3];
            fields[0] = tviCode;
            fields[1] = makeOperand(operand1);
            fields[2] = makeOperand(operand2);
            this.quads.addQuad(fields);
            this.quads.incrementNextQuad();
        }      
            
        public void setDebug(boolean bool){
            this.debug = bool;
        }

        /*
        // Execute semantic action with most recently used token, include lineNumber as parameter
        // for error handling purposes.
        */
	public void Execute (SemanticAction action, Token token, int lineNumber, String currLine)  throws SemanticError {
		
		int actionNumber = action.getIndex();
		
                // Print semantic action info for testing/debugging purposes
                if (this.debug){
                    System.out.println("calling action : " + actionNumber + " with token " + token.getType() + ", " + token.getValue());
                    System.out.println("-----");
                    System.out.println("NEXT:" + this.nextParms.toString());
                    System.out.println("PARMS:" + this.parms);
                    System.out.println(currLine);
                }
		switch (actionNumber)
		{
		
		case 1  : {
                    this.insert = true;
                    break;
		}
                
                case 2: {
                    this.insert = false;
                    break;
                }
                
                case 3: {
                    Token tok = (Token) this.semanticStack.pop();
                    TokenType tokType = tok.getType();
                    if (this.isArray){
                        Token pop1 = (Token) this.semanticStack.pop();
                        Token pop2 = (Token) this.semanticStack.pop();
                            // Insert arrayEntrys into appropriate symbol table
                            int upperBound = Integer.parseInt((String) pop1.getValue());
                            int lowerBound = Integer.parseInt((String) pop2.getValue());
                            int memSize = (upperBound - lowerBound) + 1;
                            while((!(this.semanticStack.empty())) && (this.semanticStack.peek() instanceof Token)){
                                Token popToken = (Token) this.semanticStack.pop();
                                ArrayEntry arrayEntry = new ArrayEntry((String)popToken.getValue(), 0, upperBound, lowerBound, tokType);
                                if (this.global){
                                    arrayEntry.setAddress(this.globalMemory);
                                    globalMemory = globalMemory + memSize;
                                    globalTable.insert(arrayEntry);
                                }
                                else{
                                    arrayEntry.setAddress(this.localMemory);
                                    localMemory = localMemory + memSize;
                                    this.localTable.insert(arrayEntry);
                                }
                           }
                    }
                    else{
                        // Insert variableEntries into appropriate symbol table
                            while((!(this.semanticStack.empty())) && (this.semanticStack.peek() instanceof Token)){
                                Token popToken = (Token) this.semanticStack.pop();                     
                                VariableEntry varEntry = new VariableEntry((String)popToken.getValue(), tokType, 0);
                                if (this.global){
                                    varEntry.setAddress(this.globalMemory);
                                    globalMemory = globalMemory + 1;
                                    globalTable.insert(varEntry);
                                }
                                else{
                                    varEntry.setAddress(this.localMemory);
                                    localMemory = localMemory + 1;
                                    this.localTable.insert(varEntry);
                                }
                    }
                    }
                    this.isArray = false;
                    break;
                }
                
                case 4: {
                    this.semanticStack.push(token);
                    break;
                }
                
                case 5:{
                    SymbolTableEntry stackEntry = (SymbolTableEntry) this.semanticStack.pop();
                    this.insert = false;
                    generate("PROCBEGIN", stackEntry);
                    this.localStore = this.quads.getNextQuad();
                    generate("alloc", "_");
                    break;
                }
                
                case 6: {
                    this.isArray = true;
                    break;
                }
                
                case 7: {
                    this.semanticStack.push(token);
                    break;
                }
                
                case 9: {
                    while(!this.semanticStack.isEmpty()){
                        Token popToken = (Token) this.semanticStack.pop();
                        VariableEntry varEntry = new VariableEntry((String)popToken.getValue(), popToken.getType(), this.globalMemory);
                        varEntry.setRestricted();
                        this.globalTable.insert(varEntry);
                        this.insert = false;
                    }
                    generate("call", this.globalTable.lookup("MAIN"), Integer.toString(0));
                    generate("exit");
                    break;
                }
                
                case 11:{
                    this.global = true;
                    this.localTable.clear();
                    this.currentFunction = null;
                    int[] backpatchList = new int[1];
                    backpatchList[0] = localStore;
                    this.backpatch(backpatchList, localMemory);
                    generate("free", Integer.toString(localMemory));
                    generate("PROCEND");
                    break;
                }
                
                case 13: {
                    this.semanticStack.push(token);
                    break;
                }  
                
                
                case 15: {
                    FunctionEntry function = new FunctionEntry(token.getValue().toString(), 0, new Stack<SymbolTableEntry>(), null);
                    this.globalTable.insert(function);
                    this.semanticStack.push(function);
                    SymbolTableEntry resultEntry = this.create(token.getValue().toString(),TokenType.INTEGER);
                    resultEntry.makeResult();
                    this.globalTable.lookup(token.getValue().toString()).setResult(this.globalTable.lookup("$$" + token.getValue().toString()));
                    this.global = false;
                    this.localTable = new SymbolTable(tableSize);
                    this.localMemory = 0;
                    break;
                }
                
                case 16: {
                    Token stackEntry = (Token) this.semanticStack.pop();
                    TokenType type = stackEntry.getType();
                    FunctionEntry function = (FunctionEntry) this.semanticStack.elementAt(0);
                    this.globalTable.lookup("$$" + function.getName()).setType(type);
                    this.currentFunction = this.globalTable.lookup(function.getName());
                    break;
                }
                    
                case 17: {
                    ProcedureEntry procedure = new ProcedureEntry(token.getValue().toString(),0, new Stack<SymbolTableEntry>());
                    this.globalTable.insert(procedure);
                    this.semanticStack.push(procedure);
                    this.global = false;
                    this.localTable = new SymbolTable(tableSize);
                    this.localMemory = 0;
                    break;
                }
                
                case 19:{
                    this.parms = new Stack<Integer>();
                    this.parms.add(0);
                    break;
                }
                
                case 20: {
                    SymbolTableEntry stackEntry = (SymbolTableEntry) this.semanticStack.peek();
                    Stack<SymbolTableEntry> parmStack = new Stack<SymbolTableEntry>();
                    Stack<SymbolTableEntry> reverseParms = this.globalTable.lookup(stackEntry.getName()).getParmInfo();
                    while(!(reverseParms.empty())){
                        SymbolTableEntry pop = reverseParms.pop();
                        parmStack.push(pop);
                    }
                    this.globalTable.lookup(stackEntry.getName()).setParmInfo(parmStack);
                    this.globalTable.lookup(stackEntry.getName()).setParms((int)this.parms.pop());
                    break;
                }
                
                case 21: {
                    Token stackEntry = (Token) this.semanticStack.pop();
                    TokenType type = stackEntry.getType();
                    SymbolTableEntry procFunEntry = (SymbolTableEntry) this.semanticStack.elementAt(0);
    
                    while(this.semanticStack.peek() instanceof Token){
                        Token param = (Token) this.semanticStack.pop();
                        if ((param.getType() == TokenType.INTCONSTANT)||(param.getType() == TokenType.REALCONSTANT)){

                            int upperBound = Integer.parseInt((String)param.getValue());
                            Token param2 = (Token) this.semanticStack.pop();
                            int lowerBound = Integer.parseInt((String)param2.getValue());
                            
              
                            Token paramArray = (Token) this.semanticStack.peek();
                            boolean done = false;
                            while(!done){
                                this.semanticStack.pop();
                                ArrayEntry paramEntry = new ArrayEntry((String)paramArray.getValue(), this.localMemory, upperBound, lowerBound, type);
                                paramEntry.setParm();
                                this.insertLocal(paramEntry, this.localTable);
                                this.globalTable.lookup(procFunEntry.getName()).addParm(paramEntry);
                                if (!(this.semanticStack.empty())){
                                if (this.semanticStack.peek() instanceof Token){
                                paramArray = (Token) this.semanticStack.peek();
                                }
                                else{
                                    done = true;
                                }
                                }
                            } 
                        }
                        else{
                            VariableEntry paramEntry = new VariableEntry((String)param.getValue(),type, this.localMemory);
                            paramEntry.setParm();
                            this.insertLocal(paramEntry, this.localTable);
          
                            this.globalTable.lookup(procFunEntry.getName()).addParm(paramEntry);
                        }

                        this.localMemory++;
                        this.isArray = false;
                        int parmCount = (int) this.parms.pop();
                        parmCount++;
                        this.parms.push(parmCount);
                    }
                    break;
                            
                }
                
                
                case 22:{
                    Etype etype = (Etype) this.semanticStack.pop();
                    if (!(etype == Etype.RELATIONAL)){
                        throw SemanticError.IllegalEtype(lineNumber, Etype.RELATIONAL, currLine); 
                    }
                    else{
                        int[] Etrue = (int[]) this.semanticStack.pop();
            
                        backpatch(Etrue, this.quads.getNextQuad());
                        this.semanticStack.push(Etrue);
                    }
                    break;
                }
                
                case 24:{
                    int beginLoop = this.quads.getNextQuad();
                    this.semanticStack.push(beginLoop);
                    break;
                }
                
                case 25: {
                    Etype etype = (Etype) this.semanticStack.pop();
                    
                    if (!(etype == Etype.RELATIONAL)){
                        throw SemanticError.IllegalEtype(lineNumber, Etype.RELATIONAL, currLine);
                    }
                    
                    int[] Etrue = (int[]) this.semanticStack.peek();
                    backpatch(Etrue, this.quads.getNextQuad());
                    
                    
                    break;
                }
                
                case 26: {
                    int[] Etrue = (int[]) this.semanticStack.pop();
                    int[] Efalse = (int []) this.semanticStack.pop();
                    int begin = (int) this.semanticStack.pop();
                    generate("goto", Integer.toString(begin));
                    backpatch(Efalse, this.quads.getNextQuad());
                    break;
                }
                
                case 27: {
                    int[] Etrue = (int[]) this.semanticStack.pop();
                    int[] Efalse = (int[]) this.semanticStack.pop();
                    int[] skipElse = makeList(this.quads.getNextQuad());
                    
                    generate("goto", "_");
                    backpatch(Efalse, this.quads.getNextQuad());
                    this.semanticStack.push(Efalse);
                    this.semanticStack.push(Etrue);
                    this.semanticStack.push(skipElse);
                    break;
                           
                }
                
                case 28: {
                    int[] skipElse = (int[]) this.semanticStack.pop();
                    int[] Etrue = (int[]) this.semanticStack.pop();
                    int[] Efalse = (int[]) this.semanticStack.pop();
                    backpatch(skipElse, this.quads.getNextQuad());
                    break;
                }
                
                case 29:{
                    int[] Etrue = (int[]) this.semanticStack.pop();
                    int[] Efalse = (int[]) this.semanticStack.pop();
                    backpatch(Efalse, this.quads.getNextQuad());
                    break;
                }
                
                case 30: {
                    String tokVal = (String) token.getValue();
                    if (this.lookup((String)token.getValue()) == null){
                        try{
                        throw SemanticError.UndeclaredVariable(token, lineNumber, currLine);
                        }
                        catch(SemanticError ex){
                           this.errors.add(ex.toString());
                        }
                        VariableEntry varEntry = new VariableEntry((String)token.getValue(), token.getType(), this.globalMemory);
                        varEntry.setError(true);
                        if (global){
                            this.globalTable.insert(varEntry);
                        }
                        else{
                            this.insertLocal(varEntry, this.localTable);
                        }
                        this.semanticStack.push(varEntry);
                        this.semanticStack.push(Etype.ARITHMETIC);
                    }
                    else{
                         this.semanticStack.push(this.lookup((String)token.getValue()));
                         this.semanticStack.push(Etype.ARITHMETIC);
                    }
                    break;
                }
                
                case 31: {
                    Etype etype = (Etype) this.semanticStack.pop();
                    SymbolTableEntry stackEntry2 = (SymbolTableEntry) this.semanticStack.pop();
                    SymbolTableEntry offset = (SymbolTableEntry) this.semanticStack.pop();
                   
                    if (this.semanticStack.peek() instanceof Etype){
                     Etype etype3 = (Etype) this.semanticStack.pop();
                    }
                    SymbolTableEntry stackEntry1  = (SymbolTableEntry) this.semanticStack.pop();
                    
                    TokenType type2 = stackEntry2.getType();
                    TokenType type1 = stackEntry1.getType();
                    
                    
                    if (!(etype == Etype.ARITHMETIC)){
                        throw SemanticError.IllegalEtype(lineNumber, Etype.ARITHMETIC, currLine);
                        
                    }
                    else{
                        if (this.typeCheck(type1, type2) == 3) {
                            throw SemanticError.IllegalOperands(type1, type2, lineNumber, currLine);
                        }
                    else if (this.typeCheck(type1,type2) == 2){
                        SymbolTableEntry tempEntry = create("TEMP", TokenType.REAL);
                        generate("ltof", stackEntry2, tempEntry);
                        if (offset instanceof NullEntry){
                            generate("move", tempEntry, stackEntry1);
                        }
                        else{
                            generate("stor", tempEntry, offset, stackEntry1);
                        }
                    }
                    else if (offset instanceof NullEntry){                       
                        generate("move", stackEntry2, stackEntry1);
                    }
                    else {
                        generate("stor", stackEntry2, offset, stackEntry1);
                        }
                    }
                    break;
                }
                
                case 32: {
                    Etype etype = (Etype) this.semanticStack.pop();
                    SymbolTableEntry stackEntry = (SymbolTableEntry) this.semanticStack.peek();
                    this.semanticStack.push(etype);
                  
                    if (!(etype == Etype.ARITHMETIC)){
                        throw SemanticError.IllegalEtype(lineNumber, Etype.ARITHMETIC, currLine);
                    }
                    
                    if (!(stackEntry instanceof ArrayEntry)){
                        throw SemanticError.IllegalArray(lineNumber, stackEntry, currLine);
                    }
                    break;
                }
                
                case 33: {                    
       
                    Etype etype = (Etype) this.semanticStack.pop();
                    SymbolTableEntry stackEntry = (SymbolTableEntry) this.semanticStack.pop();
                    
                    if (!(etype == Etype.ARITHMETIC)){
                        throw SemanticError.IllegalEtype(lineNumber, Etype.ARITHMETIC, currLine);
                    }
                    if (!(stackEntry.getType() == TokenType.INTEGER)){
                        throw SemanticError.IllegalVariableOffset(token, lineNumber, currLine);
                    }
                    else{
                        SymbolTableEntry tempEntry = create("TEMPa", TokenType.INTEGER);
                        ArrayEntry bottomStack = null;
                        boolean found = false;
                        int index = 0;
                        while (!found && (index < this.semanticStack.size())){
                            if (this.semanticStack.elementAt(index) instanceof ArrayEntry){
                                bottomStack = (ArrayEntry) this.semanticStack.elementAt(index);
                                found = true;
                            }
                            else{
                                index++;
                            }
                        }
                        if (bottomStack == null){
                            SymbolTableEntry bottom = (SymbolTableEntry) this.semanticStack.elementAt(0);
                            try{
                                throw SemanticError.IllegalArray(lineNumber, bottom, currLine);
                            }
                            catch(SemanticError ex){
                                if (!bottom.getError()){
                                    bottom.setError();
                                }
                            }
                            this.semanticStack.push(tempEntry);
                        }
                        else{
                        generate("sub", stackEntry, new ConstantEntry(Integer.toString(bottomStack.getLowerBound()), TokenType.INTEGER),tempEntry);
                        this.semanticStack.push(tempEntry);
                        }
                    }
                    break;
                }
                
                case 34: {
                    Etype etype = (Etype) this.semanticStack.pop();
                    SymbolTableEntry stackEntry = (SymbolTableEntry) this.semanticStack.pop();
                   
                    if (stackEntry instanceof FunctionEntry){
                        stackEntry.setParms(0);
                        this.semanticStack.push(stackEntry);
                        this.Execute(SemanticAction.action52,token, lineNumber, currLine);
                    }
                    else{
                        this.semanticStack.push(stackEntry);
                        this.semanticStack.push(new NullEntry("null"));
                }
                    break;
                }
                
                case 35: {
                    this.parms.push(0);
                    Etype type = (Etype) this.semanticStack.pop();
                    SymbolTableEntry procFunEntry = null;
                    for (int i = 0; i < this.semanticStack.size(); i++){
                        if ((this.semanticStack.elementAt(i) instanceof ProcedureEntry)||
                                (this.semanticStack.elementAt(i) instanceof FunctionEntry)){
                             procFunEntry = (SymbolTableEntry) this.semanticStack.elementAt(i);
                             break;
                        }
                    }
                    Stack<SymbolTableEntry> parmInfo = procFunEntry.getParmInfo();
                    this.nextParms.push(parmInfo);
                    this.semanticStack.push(type);
                    break;
                }
                
                case 36: {
                    Etype etype = (Etype) this.semanticStack.pop();
                    SymbolTableEntry procFunEntry = (SymbolTableEntry) this.semanticStack.pop();
                    int paramNum = procFunEntry.getParameters();
                    if (!(paramNum == 0)){
                        throw SemanticError.IllegalParameters(lineNumber, paramNum, currLine);
                    }
                    else{
                        generate("call", procFunEntry, Integer.toString(0));
                    }
                    break;
                }
                
                case 37: {
                    Etype etype = (Etype) this.semanticStack.pop();
                    SymbolTableEntry stackEntry = (SymbolTableEntry) this.semanticStack.pop();
                    
                    int index =0;
                    for (int i = 0; i < this.semanticStack.size(); i++){
                        if ((this.semanticStack.elementAt(i) instanceof FunctionEntry)|| 
                                (this.semanticStack.elementAt(i) instanceof ProcedureEntry)){
                            index = i;
                            break;
                        }
                    }
                    SymbolTableEntry procFunEntry = (SymbolTableEntry) this.semanticStack.elementAt(index);
                    SymbolTableEntry expectedParam = new NullEntry("null");            
                    
                    if (!this.nextParms.peek().isEmpty()){
                     if ((this.nextParms.peek().size() - 1 - this.parmInfoCount) < 0){
                         throw SemanticError.IllegalParameters2(lineNumber, this.nextParms.peek().size(), currLine);
                     }
                     expectedParam = this.nextParms.peek().elementAt(this.nextParms.peek().size() - 1 - this.parmInfoCount);
                    }
                    if (!((stackEntry instanceof VariableEntry)||(stackEntry instanceof ConstantEntry)||
                            (stackEntry instanceof ArrayEntry) || (stackEntry.isFuncResult()))){
                        throw SemanticError.IllegalResult(lineNumber, stackEntry, currLine);
                    }
                    else{
                        int parmCount = (int) this.parms.pop();
                        parmCount++;
                        this.parms.push(parmCount);
                        if (!((procFunEntry.getName().equals("READ")) || (procFunEntry.getName().equals("WRITE")))){
                            int parmCountTop = (int) this.parms.peek();

                            if (parmCountTop > procFunEntry.getParameters()){
                                throw SemanticError.MismatchedParameters(lineNumber, procFunEntry.getParameters(), parmCountTop, currLine); 
                            }
                            TokenType expectedParamType = expectedParam.getType();
                            if (!(stackEntry.getType() == expectedParamType)){
                                throw SemanticError.MismatchedParameterType(lineNumber, expectedParamType, stackEntry, currLine);
                            }
                            if (expectedParam instanceof ArrayEntry){
                                ArrayEntry expectedArrayParam = (ArrayEntry) expectedParam;
                                ArrayEntry actualArrayParam = (ArrayEntry) stackEntry;
                                if (!(expectedArrayParam.getLowerBound() == actualArrayParam.getLowerBound())){
                                    throw SemanticError.MismatchedArrayParameter(lineNumber, expectedArrayParam.getLowerBound(), actualArrayParam.getLowerBound(), currLine);
                                }
                                if (!(expectedArrayParam.getUpperBound() == actualArrayParam.getUpperBound())){
                                    throw SemanticError.MismatchedArrayParameter(lineNumber, expectedArrayParam.getUpperBound(), actualArrayParam.getUpperBound(), currLine);
                                }
                            }
                        }
                    }
                    
                    this.semanticStack.push(stackEntry);
                    this.semanticStack.push(etype);
                    this.parmInfoCount++;
                    
                    break;
                }
                
                
                case 38: {
                    
                    Etype etype = (Etype) this.semanticStack.pop();
                    if (!(etype == Etype.ARITHMETIC)){
                        throw SemanticError.IllegalEtype(lineNumber, Etype.ARITHMETIC, currLine);
                    }
                    
                    this.semanticStack.push(token);
                    break;
                            
                }
                
                case 39:{
                    Etype type = (Etype) this.semanticStack.pop();
                    
                    if (!(type == Etype.ARITHMETIC)){
                        throw SemanticError.IllegalEtype(lineNumber, Etype.ARITHMETIC, currLine);
                    }
                    
                    SymbolTableEntry stackEntry2 = (SymbolTableEntry) this.semanticStack.pop();
                    Token opToken = (Token) this.semanticStack.pop();
                    SymbolTableEntry stackEntry1 = (SymbolTableEntry) this.semanticStack.pop();
                    
                    TokenType type1 = stackEntry1.getType();
                    TokenType type2 = stackEntry2.getType();
                    
                    if (this.typeCheck(type1, type2) == 2){
                        SymbolTableEntry tempEntry = create("TEMPb", TokenType.REAL);
                        generate("ltof", stackEntry2, tempEntry);
                        generate(opToken.getTVIOpType(), stackEntry1, tempEntry, "_");
                    }
                    else if (this.typeCheck(type1, type2) == 3){
                        SymbolTableEntry tempEntry = create("TEMPc",TokenType.REAL);
                        generate("ltof", stackEntry1, tempEntry);
                        generate(opToken.getTVIOpType(), tempEntry, stackEntry2, "_");
                    }
                    else{
                        generate(opToken.getTVIOpType(),stackEntry1, stackEntry2, "_");
                    }
                    generate("goto", "_");
                    
                    int[] Etrue = makeList(this.quads.getNextQuad()-2);
                    int[] Efalse = makeList(this.quads.getNextQuad() - 1);
                    
                    this.semanticStack.push(Efalse);
                    this.semanticStack.push(Etrue);
                    this.semanticStack.push(Etype.RELATIONAL);
                    
                    break;
                }
                
                case 40: {
                    this.semanticStack.push(token);
                    break;
                }
                    
                case 41:{
                    Etype etype = (Etype) this.semanticStack.pop();
                    SymbolTableEntry stackEntry = (SymbolTableEntry) this.semanticStack.pop();
                    Token sign = (Token) this.semanticStack.pop();
                    if (sign.getType() == TokenType.UNARYMINUS){
                        SymbolTableEntry tempEntry = create("TEMPd", stackEntry.getType());
                        generate("uminus", stackEntry, tempEntry);
                        this.semanticStack.push(tempEntry);
                    }
                    else{
                        this.semanticStack.push(stackEntry);
                    }
                    this.semanticStack.push(etype);
                     
                    
                    break;
                }
                
                case 42: {
                    Etype etype = (Etype) this.semanticStack.pop();
                   
                    if (token.getOpType() == "BOOLEAN OR")
                         if (!(etype == Etype.RELATIONAL)) {
                             throw SemanticError.IllegalEtype(lineNumber, Etype.RELATIONAL, currLine);
                                     }
                         else{
                             int[] Etrue = (int[]) this.semanticStack.pop();
                             int[] Efalse = (int[]) this.semanticStack.peek();
                             this.semanticStack.push(Etrue);
                             this.backpatch(Efalse,this.quads.getNextQuad());
                         }
                
                    else if (!(etype == Etype.ARITHMETIC)){
                        throw SemanticError.IllegalEtype(lineNumber, Etype.ARITHMETIC, currLine);
                    }
             
                    this.semanticStack.push(token);
                    break; 
                }
                
                case 43: {
      
                    
                    Token opToken = null;
                    SymbolTableEntry stackEntry1 = null;
                    SymbolTableEntry stackEntry2 = null;
                    int[] Etrue1,Etrue2,Efalse1,Efalse2;
                    Etrue1 = null;
                    Etrue2 = null;
                    Efalse1 = null;
                    Efalse2 = null;
                            
                    Etype etype = (Etype) this.semanticStack.pop();
                    
                    if (this.semanticStack.peek() instanceof int[]){
                    
                    Etrue2 = (int[]) this.semanticStack.pop();
                    Efalse2 = (int[]) this.semanticStack.pop();
                    opToken = (Token) this.semanticStack.pop();
                    Etrue1 = (int[]) this.semanticStack.pop();
                    Efalse1 = (int[]) this.semanticStack.pop();
                    }
                    else{
                     stackEntry2 = (SymbolTableEntry) this.semanticStack.pop();
                     opToken = (Token) this.semanticStack.pop();
                     stackEntry1 = (SymbolTableEntry) this.semanticStack.pop();
                                
                    }
                    
                    if (etype == Etype.RELATIONAL){
                        if (opToken.getOpType() == "BOOLEAN OR"){
                           int[] Etrue = this.merge(Etrue1, Etrue2);
                           int[] Efalse = Efalse2;
                           this.semanticStack.push(Efalse);
                           this.semanticStack.push(Etrue);
                           this.semanticStack.push(Etype.RELATIONAL);
                        }
                    }
                    else{

                        TokenType type1 = stackEntry1.getType();
                        TokenType type2 = stackEntry2.getType();
               
                    int typeCheck = typeCheck(type1, type2);
                    String opType = opToken.getTVIOpType();
                    SymbolTableEntry tempEntry = null;
                    
                    
               
                    if (typeCheck == 0){   
                        tempEntry = create("TEMPe", TokenType.INTEGER);
                        generate(opType, stackEntry1, stackEntry2, tempEntry);
                    }
                    else if (typeCheck == 1){
                        tempEntry = create("TEMPf", TokenType.REAL);
                        generate("f" + opType, stackEntry1, stackEntry2, tempEntry);
                    }
                     else if (typeCheck == 2){
                        SymbolTableEntry tempEntry1 = create("TEMPg", TokenType.REAL);
                        generate("ltof", stackEntry2, tempEntry1);
                        tempEntry = create("TEMPh", TokenType.REAL);
                        generate("f" + opType, stackEntry1, tempEntry1, tempEntry);
                    }
                     else  {
                         SymbolTableEntry tempEntry1 = create("TEMP1", TokenType.REAL);
                         generate("ltof", stackEntry1, tempEntry1);
                         tempEntry = create("TEMPi", TokenType.REAL);
                         generate("f" + opType, tempEntry1, stackEntry2, tempEntry);
                     }
                    
                    
                    this.semanticStack.push(tempEntry);
                    this.semanticStack.push(Etype.ARITHMETIC);
                    }
                    break;
                }
                
                case 44: {
                    Etype etype = (Etype) this.semanticStack.pop();
                    
                    if (token.getOpType() == "BOOLEAN AND"){
                        int[] Etrue = (int[]) this.semanticStack.peek();
                        backpatch(Etrue, this.quads.getNextQuad());
                    }
                    this.semanticStack.push(token);
                    break;
                }
                
                
                case 45: {
                    
                    
                    Token opToken = null;
                    SymbolTableEntry stackEntry1 = null;
                    SymbolTableEntry stackEntry2 = null;
                    int[] Etrue1 = null;
                    int[] Etrue2 = null;
                    int[] Efalse1 = null;
                    int[] Efalse2 = null;
                    Etype etype = (Etype) this.semanticStack.pop();
                    
                    if (this.semanticStack.peek() instanceof int[]){
                    
                     Etrue2 = (int[]) this.semanticStack.pop();
                     Efalse2 = (int[]) this.semanticStack.pop();
                     opToken = (Token) this.semanticStack.pop();
                     Etrue1 = (int[]) this.semanticStack.pop();
                     Efalse1 = (int[]) this.semanticStack.pop();
                    }
                    else{
                    stackEntry2 = (SymbolTableEntry) this.semanticStack.pop();
                    opToken = (Token) this.semanticStack.pop();
                    stackEntry1 = (SymbolTableEntry) this.semanticStack.pop();
                    }
            
                
                    if (opToken.getOpType() == "BOOLEAN AND"){
                        if (!(etype == Etype.RELATIONAL)){
                            throw SemanticError.IllegalEtype(lineNumber, Etype.RELATIONAL, currLine);
                        }
                        else{
                            int[] Etrue = Etrue2;
                            int[] Efalse = this.merge(Efalse1, Efalse2);
                            this.semanticStack.push(Efalse);
                            this.semanticStack.push(Etrue);
                            this.semanticStack.push(Etype.RELATIONAL);
                        }
                    }
                    else{   
                    
                    TokenType type1 = stackEntry1.getType();
                    TokenType type2 = stackEntry2.getType();
               
                    int typeCheck = typeCheck(type1, type2);
                    String opType = opToken.getTVIOpType();
                    SymbolTableEntry tempEntry;
                    
                    if ((opType == "mod") && (typeCheck != 0)){
                        throw SemanticError.IllegalOperands(type1, type2, lineNumber, currLine);
                    }
                    else if (typeCheck == 0){
                        if (opType == "mod"){
                            tempEntry = create("TEMPj", TokenType.INTEGER);
                           
                            generate("move", stackEntry1, tempEntry);
                            SymbolTableEntry tempEntry2 = create("TEMPk", TokenType.INTEGER);
                            generate("move", tempEntry, tempEntry2);
                            generate("sub", tempEntry2, stackEntry2, tempEntry);
                            generate("bge", tempEntry, stackEntry2, Integer.toString(this.quads.getNextQuad() - 2));
                        }
                        else {
                            if (opType == "div"){
                                SymbolTableEntry tempEntry1 = create("TEMPl", TokenType.REAL);
                                generate("ltof", stackEntry1, tempEntry1);
                                SymbolTableEntry tempEntry2 = create("TEMPm", TokenType.REAL);
                                generate("ltof", stackEntry2, tempEntry2);
                                tempEntry = create("TEMPn", TokenType.REAL);
                                generate("fdiv", tempEntry1, tempEntry2, tempEntry);
                            }
                            else{
                                tempEntry = create("TEMPo", TokenType.INTEGER);
                                generate(opType, stackEntry1, stackEntry2, tempEntry);
                            }
                        }
                    }
                    else if (typeCheck == 1){
                        if (opType == "div"){
                            SymbolTableEntry tempEntry1 = create("TEMPp", TokenType.INTEGER);
                            generate("ftol", stackEntry1, tempEntry1);
                            SymbolTableEntry tempEntry2 = create("TEMPq", TokenType.INTEGER);
                            generate("ftol", stackEntry2, tempEntry2);
                            tempEntry = create("TEMP3", TokenType.INTEGER);
                            generate("div", tempEntry1, tempEntry2, tempEntry);
                        }
                        else{
                            tempEntry = create("TEMPr", TokenType.REAL);
                            generate("f" + opType, stackEntry1, stackEntry2, tempEntry);
                        }
                    }
                    else if (typeCheck == 2){
                        if (opType == "div"){
                            SymbolTableEntry tempEntry1 = create("TEMPs", TokenType.INTEGER);
                            generate("ftol", stackEntry1, tempEntry1);
                            tempEntry = create("TEMP2", TokenType.INTEGER);
                            generate("div", tempEntry1, stackEntry2, tempEntry);
                        }
                        else{
                            SymbolTableEntry tempEntry1 = create("TEMPt", TokenType.REAL);
                            generate("ltof", stackEntry2, tempEntry1);
                            tempEntry = create("TEMPu", TokenType.REAL);
                            generate("f" + opType, tempEntry1, tempEntry);
                        }
                    }
                    else {
                        if (opType == "div"){
                            SymbolTableEntry tempEntry1 = create("TEMPv", TokenType.INTEGER);
                            generate("ftol", stackEntry2, tempEntry1);
                           tempEntry= create("TEMP2", TokenType.INTEGER);
                            generate("div", stackEntry1, tempEntry1, tempEntry);
                        }
                        else{
                            SymbolTableEntry tempEntry1 = create("TEMPw", TokenType.REAL);
                            generate("ltof", stackEntry1, tempEntry1);
                            tempEntry = create("TEMPx", TokenType.REAL);
                            generate("f" + opType, tempEntry1, stackEntry2, tempEntry);
                        }
                    }
                    this.semanticStack.push(tempEntry);
                    this.semanticStack.push(Etype.ARITHMETIC);
                    }
                    break;
                }
                
                case 46: {
             
                    if (token.getType() == TokenType.IDENTIFIER){
                        if (this.lookup((String) token.getValue()) == null){
                            try{
                            throw SemanticError.UndeclaredVariable(token, lineNumber, currLine);
                            }
                            catch(SemanticError ex){
                                this.errors.add(ex.toString());
                            }
                        VariableEntry varEntry = new VariableEntry((String)token.getValue(), token.getType(), this.globalMemory);
                        varEntry.setError(true);
                        if (global){
                            this.globalTable.insert(varEntry);
                        }
                        else{
                            this.insertLocal(varEntry, this.localTable);
                        }
                        this.semanticStack.push(varEntry);
                        }
                        else{
                            this.semanticStack.push(this.lookup((String) token.getValue()));
                        }}
                    else if ((token.getType() == TokenType.INTCONSTANT) || (token.getType() == TokenType.REALCONSTANT)){
                        if (this.constantTable.lookup((String) token.getValue()) == null){
                            if (token.getType() == TokenType.INTCONSTANT){
                                 this.constantTable.insert(new ConstantEntry((String)token.getValue(), TokenType.INTEGER));
                            }
                            else{
                                this.constantTable.insert(new ConstantEntry((String) token.getValue(), TokenType.REAL));
                            }
                            }
                        this.semanticStack.push(this.constantTable.lookup((String) token.getValue()));
                    }
                    
                    this.semanticStack.push(Etype.ARITHMETIC);
                    
                    
                    
                    break;
                }
                
                case 47: {
                    Etype etype = (Etype) this.semanticStack.pop();
                    
                    if (!(etype == Etype.RELATIONAL)){
                        throw SemanticError.IllegalEtype(lineNumber, Etype.RELATIONAL, currLine);
                    }
                    
                    else{
                        int[] Etrue = (int[])this.semanticStack.pop();
                        int[] Efalse = (int[]) this.semanticStack.pop();
                        
                        this.semanticStack.push(Efalse);
                        this.semanticStack.push(Etrue);
                        
                        this.semanticStack.push(Etype.RELATIONAL);
                    }
                    
                    break;
                }
                
                
                
                case 48: {
                    if (this.semanticStack.peek() instanceof Etype){
                        this.semanticStack.pop();
                    }
                    SymbolTableEntry offsetEntry = (SymbolTableEntry) this.semanticStack.peek();
                   
                    if (!(offsetEntry instanceof NullEntry)){
                        if(!(offsetEntry.getType() == TokenType.INTEGER)){
                            throw SemanticError.IllegalVariableOffset(offsetEntry, lineNumber, currLine);
                        }
                        else{   this.semanticStack.pop();   
                                Etype etype = (Etype) this.semanticStack.pop();
                    
                                SymbolTableEntry stackEntry = (SymbolTableEntry) this.semanticStack.pop();
                                SymbolTableEntry tempEntry = create("TEMPy", stackEntry.getType());
                                generate("load", stackEntry, offsetEntry, tempEntry);
                               
                                this.semanticStack.push(tempEntry); 
                               
                         }
                    }
                    else{
                        this.semanticStack.pop();
                    }
                    this.semanticStack.push(Etype.ARITHMETIC);
                    break;
                }
                
                case 49:{
                    Etype etype = (Etype) this.semanticStack.pop();
                    if (!(etype == Etype.ARITHMETIC)){
                        throw SemanticError.IllegalEtype(lineNumber, Etype.ARITHMETIC, currLine);
                    }
                    SymbolTableEntry funcEntry = (SymbolTableEntry) this.semanticStack.peek();
                    if (!(funcEntry instanceof FunctionEntry)){
                        throw SemanticError.ExpectingFunction(lineNumber, funcEntry, currLine);
                    }
                    
                    this.parms.push(0);
                    this.nextParms.push(funcEntry.getParmInfo());
                    this.parmInfoCount = 0;
                    this.semanticStack.push(etype);
                    break;
                }
                
                case 50: {
                    
                    ArrayList<SymbolTableEntry> poppedObjects = new ArrayList<SymbolTableEntry>();
                    while (!(this.semanticStack.peek() instanceof FunctionEntry)){
                        Object stackEntry = this.semanticStack.pop();
                        if (stackEntry instanceof SymbolTableEntry && (!(stackEntry instanceof NullEntry))){
                                SymbolTableEntry idEntry = (SymbolTableEntry) stackEntry;
                                poppedObjects.add(idEntry);
                    }
                    }
                    FunctionEntry funcEntry = (FunctionEntry) this.semanticStack.pop();
                        
                    for (int i = 0; i < poppedObjects.size(); i++){
                        generate("param", poppedObjects.get(i));
                        this.localMemory++;
                    }

                    if (!(this.parms.peek() == funcEntry.getParameters())){
                        throw SemanticError.MismatchedParameters(lineNumber, funcEntry.getParameters(), this.parms.peek(), currLine);
                    }
   
                    int parmCount = this.parms.pop();
                    generate("call", funcEntry, Integer.toString(parmCount));
                    this.nextParms.pop();
                    this.parmInfoCount = 0;
                    SymbolTableEntry tempEntry = create("TEMP", funcEntry.getResult().getType());
                    generate("move", funcEntry.getResult(), tempEntry);
                    
  
                    this.semanticStack.push(tempEntry);
                    this.semanticStack.push(Etype.ARITHMETIC);
                            
                    
                    break;
                }
                    
                    
                case 51: {
                    Etype etype = (Etype) this.semanticStack.pop();
                    ArrayList<SymbolTableEntry> params = new ArrayList<SymbolTableEntry>();
                    SymbolTableEntry procedure = null;
                    for (int i = 0; i < this.semanticStack.size(); i++){
                        if (this.semanticStack.elementAt(i) instanceof ProcedureEntry){
                             procedure = (SymbolTableEntry) this.semanticStack.elementAt(i);
                             break;
                        }
                    }
                        if (procedure.getName() == "WRITE"){
                            this.semanticStack.push(etype);
                            this.Execute(SemanticAction.action57, token, lineNumber, currLine);
                        }
                        else if (procedure.getName() == "READ"){
                            this.semanticStack.push(etype);
                            this.Execute(SemanticAction.action58, token, lineNumber, currLine);
                        }
                        else{
                            if (!(parms.peek() == procedure.getParameters())){
                                throw SemanticError.MismatchedParameters(lineNumber, procedure.getParameters(), parms.peek(), currLine);
                            }
                            else{
                                
                                while(!(this.semanticStack.peek() == procedure)){
                                    Object stackEntry = this.semanticStack.pop();
                                    if (stackEntry instanceof SymbolTableEntry){
                                        SymbolTableEntry paramEntry = (SymbolTableEntry) stackEntry;
                                        params.add(paramEntry);
                                    }
                                }
                                this.semanticStack.pop();
                                for (int i = 0; i < params.size(); i++){
                                    generate("param", params.get(i));
                                    this.localMemory = this.localMemory + 1;
                                }
                                int parmCount = this.parms.pop();
                                generate("call", procedure, Integer.toString(parmCount));
                                this.nextParms.pop();
                            }
                        }
                    
                    
                    
                    break;
                }
                    
                case 52:{
                    //Etype etype = (Etype) this.semanticStack.pop();
                    SymbolTableEntry function = (SymbolTableEntry) this.semanticStack.pop();
                    if (!(function instanceof FunctionEntry)){
                        throw SemanticError.ExpectingFunction(lineNumber, function, currLine);
                    }
                    if (function.getParameters() > 0){
                        throw SemanticError.MismatchedParameters(lineNumber, 0, function.getParameters(), currLine);
                    }
                        generate("call", function, Integer.toString(0));
                        SymbolTableEntry tempEntry = create("TEMPz", function.getType());
                        generate("move", function.getResult(), tempEntry);
                        this.semanticStack.push(tempEntry);
                        this.semanticStack.push(Etype.ARITHMETIC);

                    break;
                }     
                
                case 53:{
                Etype etype = (Etype) this.semanticStack.pop();
                SymbolTableEntry stackEntry = (SymbolTableEntry) this.semanticStack.peek();
                if (stackEntry instanceof FunctionEntry){
                    FunctionEntry function = (FunctionEntry) stackEntry;
                    if (!(function.getName() == currentFunction.getName())){
                        throw SemanticError.IllegalFunction(lineNumber, currentFunction.getName(), function, currLine);
                    }
                        else{
                           this.semanticStack.pop();
                           this.semanticStack.push(function.getResult());
                           this.semanticStack.push(etype);
                    }
                    }
                else{
                    this.semanticStack.push(etype);
                }
                    break;
                }
                
                case 54: {
                    this.parmInfoCount = 0;
                    Etype etype = (Etype) this.semanticStack.pop();
                    SymbolTableEntry stackEntry = (SymbolTableEntry) this.semanticStack.peek();
                    this.semanticStack.push(etype);
                    if (!(stackEntry instanceof ProcedureEntry)){
                        throw SemanticError.IllegalProcedure(lineNumber, stackEntry, currLine);
                    }
                    
                    break;
                }
                
                case 55: {
                    int[] store = new int[1];
                    store[0] = globalStore;
                    backpatch(store, globalMemory);
                    quads.setField(this.globalStore, 1, Integer.toString(this.globalMemory));
                    generate("free", Integer.toString(globalMemory));
                    generate("PROCEND");
                    break;
                }
                
                case 56: {
                    generate("PROCBEGIN", this.globalTable.lookup("MAIN"));
                    this.globalStore = this.quads.getNextQuad();
                    generate("alloc", Integer.toString(0));
                    break;
                }
                
                //51WRITE
                case 57:{
                    ArrayList<Object> poppedObjects = new ArrayList<Object>();
                    ProcedureEntry procedure = null;
                    boolean found = false;
                    while (!found){
                        Object popped = this.semanticStack.pop();
                        if (popped instanceof ProcedureEntry){
                            procedure = (ProcedureEntry) popped;
                            found = true;
                        }
                        else{
                            poppedObjects.add(popped);
                        }
                    }
                    
                    for (int i = poppedObjects.size() - 1; i >= 0; i--){
                    if (poppedObjects.get(i) instanceof SymbolTableEntry){
                        SymbolTableEntry stackEntry = (SymbolTableEntry) poppedObjects.get(i);
                    generate("print", "\"" + stackEntry.getName() + " = \"");
                    if (stackEntry.getType() == TokenType.REAL){
                        generate("foutp", stackEntry);
           
                    }
                    else{
                        generate("outp", stackEntry);
                    }
                    generate("newl");
                    }
                    }
                    this.parmInfoCount = 0;
                    this.parms.pop();
                    this.nextParms.pop();
                    
                    break;
                }
                
                //51READ
                case 58:{
                    ArrayList<Object> poppedObjects = new ArrayList<Object>();
                    ProcedureEntry procedure = null;
                    boolean found = false;
                    while (!found){
                        Object popped = this.semanticStack.pop();
                        if (popped instanceof ProcedureEntry){
                            procedure = (ProcedureEntry) popped;
                            found = true;
                        }
                        else{
                            poppedObjects.add(popped);
                        }
                    }
                    
                    for (int i = poppedObjects.size() - 1; i >= 0; i--){
                    if (poppedObjects.get(i) instanceof SymbolTableEntry){
                        SymbolTableEntry stackEntry = (SymbolTableEntry) poppedObjects.get(i);

                    if (stackEntry.getType() == TokenType.REAL){
                        generate("finp", stackEntry);
           
                    }
                    else{
                        generate("inp", stackEntry);
                    }
                    }
                    }
                    this.parmInfoCount = 0;
                    this.nextParms.pop();
                    this.parms.pop();
                    break;
                    
                    
                }
                    
                
                }
        }
        
        public ArrayList<String> getErrors(){
            return this.errors;
        }
        
        
      
        }
		



