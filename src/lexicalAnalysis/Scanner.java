package lexicalAnalysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Scanner {
	public static int symbol_pos = 0;
	public static Map<String, Integer> symbol = new HashMap<String, Integer>();
	//关键字：种别码从6开始
	public static String keywords[] = {"int","float","double", "if","else", "do", "while", 
			"continue","break","typedef","struct","const","char","static"};
//	public static String keywordstoken[] = { "INT","FLOAT", "DOUBLE", "IF","ELSE","DO","WHILE",
//			"CONTINUE","BREAK","TYPEDEF","STRUCT","CONST","CHAR","STATIC"};
	//运算符
	public static char operator[] = { '+', '-', '*', '=', '<', '>', '&', '|', '~',  
	         '^', '!', '%'};
	//种别码：20开始
	public static String operatortoken[] = {"+","-","*","/","<",">","&","|","~","^","!",
			"%","++","--",">>","<<","&&","||","!=",">=","<=","+=","-=","*=","/="};
	//运算符后可加等于
	public static Boolean isPlusEqu(char ch)   
	{  
	     return ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '='  
	            || ch == '>' || ch == '<' || ch == '&' || ch == '|'  
	            || ch == '^';  
	 }
	//可以连续两个运算符一样
	public static Boolean isPlusSame(char ch) {
		 return ch == '+' || ch == '-' || ch == '&' || ch == '|' || ch == '<' || ch == '>';  
	}
	//界符：种别码45开始
	public static char boundary[] = { '=', ',', ';', '[', ']', '(', ')', '{', '}'};
	public static String boundarytoken[] = { "=", ",", ";", "[", "]", "(", ")", "{", "}"};
	//读文件
	public static ArrayList<String> readfile(File file){
		ArrayList<String> strings=new ArrayList<String>();
	       try{
	           BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
	           String s = null;
	            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
	               strings.add(s);
	            }
	            br.close();    
	        }catch(Exception e){
	            e.printStackTrace();
	        }
	       return strings;
	   }
		
	//判断字母及下划线
	public static Boolean isAlpha(char ch)
	{
		return ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || ch == '_');
	}
	//判断数字
	public static Boolean isDigit(char ch)
	{  
        return (ch >= '0' && ch <= '9');  
    }
	//判断是否是运算符 
	public static Boolean isOp(char ch) 
	{  
	      for (int i = 0; i < operator.length; i++) {
	    	 if (ch == operator[i]) {  
	               return true;  
	           }   
	      }	       
	        return false;  
	 }
	//判断是否是界符，‘=’已经在运算符里处理过了，所以现在不考虑等号
	public static Boolean isBound(char ch) {
		 for (int i = 0; i < boundary.length; i++) {
			 if (ch == boundary[i] && ch!='=') {  
		               return true;  
		        }   
		 }      
		        return false;  
	}
	//判断关键字,返回该关键字在关键字数组中的位置
	public static int isMatchKeyword(String str) {  
      //  Boolean flag = false;  
		int n=-1;
        for (int i = 0; i < keywords.length; i++) {  
            if (str.equals(keywords[i])) {  
                n=i;  
                break;  
            }  
        }  
        return n;  
    }
	//判断运算符字在运算符数组中的位置
	public static int isMatchOP(String str) {   
			int n=-1;
	        for (int i = 0; i < operatortoken.length; i++) {  
	            if (str.equals(operatortoken[i])) {  
	                n=i;  
	                break;  
	            }  
	        }  
	        return n;  
	    }
	//判断运算符字在运算符数组中的位置
	public static int isMatchbound(String str) { 
					int n=-1;
			        for (int i = 0; i < boundarytoken.length; i++) {  
			            if (str.equals(boundarytoken[i])) {  
			                n=i;  
			                break;  
			            }  
			        }  
			        return n;  
			    }
	//数字常量DFA获得下一个状态
	public static int digitGetNextStrustate(int startstate,char ch) {
		int nextstate=7;
		switch (startstate) {
		case 1:
			if(isDigit(ch))
				nextstate =1;
			else if(ch == 'e')
				nextstate=4;
			else if(ch == '.')
				nextstate=2;
		case 2:
			if(isDigit(ch))
				nextstate =3;
		case 3:
			if(isDigit(ch))
				nextstate =3;
			else if(ch == 'e')
				nextstate=4;
		case 4:
			if(ch == '-' || ch == '+' || isDigit(ch))
				nextstate=5;
		case 5:
			if(isDigit(ch))
				nextstate =6;
		case 6:
			if(isDigit(ch))
				nextstate =6;			
		}
		return nextstate;
	}
		
	public static void main(String[] args) {
		String filepath="input.txt";
		File file=new File(filepath);
		symbol.clear();
		//symbol_pos = 0;
		ArrayList<String> texts=new ArrayList<String>();
		texts=readfile(file);
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter("token.txt"));
			BufferedWriter out1 = new BufferedWriter(new FileWriter("symbol.txt"));
			BufferedWriter out2 = new BufferedWriter(new FileWriter("error.txt"));
            //symbol.txt是符号表
//			for(int j = 0; j < texts.size(); j++) {
			String str = "";
			for(int j = 0; j < texts.size(); j++) {
				str=texts.get(j);
			}
//				if(str.equals(""))
//					continue;//若有一行为空则直接跳过
//				else {
					//将字符串转化为字符串数组
					char[] strline = str.toCharArray();
					for(int i= 0; i < strline.length; i++) {
						//遍历strline中的每个字符
						char ch = strline[i];
						//初始化token字符串为空
						String token = "";
						//判断标识和关键字
						if (isAlpha(ch)) {
							do {  
	                            token += ch;  
	                            i++;  
	                            if(i >= strline.length) break;  
	                            ch = strline[i];  
	                        } while (ch != '\0' && (isAlpha(ch) || isDigit(ch))); 
							--i;//指针回退，列计数减1
							//是关键字
							int pos=isMatchKeyword(token.toString());
							if(pos!=-1) {
								//如果符号表为空或符号表中不包含当前token，则加入
								if (symbol.isEmpty() || (!symbol.isEmpty() && !symbol.containsKey(token))) 
	                        	{  
	                                symbol.put(token, symbol_pos);  
	                                out1.write(token+"\t"+symbol_pos+"\n");
	                                symbol_pos++;
	                            }
								out.write(token+"\t<"+(pos+6)+", _ >\n");
								System.out.println(token+"\t<"+(pos+6)+", _ >");
							}
							//是标识符,标识符的种别码为1
							else {
								//如果符号表为空或符号表中不包含当前token，则加入
								if (symbol.isEmpty() || (!symbol.isEmpty() && !symbol.containsKey(token))) 
	                        	{  
	                                symbol.put(token, symbol_pos);  
	                                out1.write(token+"\t"+symbol_pos+"\n");
	                                symbol_pos++;
	                            }
								out.write(token+"\t<1 , "+token+" >\n");
								System.out.println(token+"\t<1 , "+token+" >");
							}
							token="";
						}
						//判断数字常量
						else if(isDigit(ch)) {
							//初始化进入1状态
							int state = 1;
							//声明计数变量
							int k;
							Boolean isfloat = false;
							while ( (ch != '\0') && (isDigit(ch) || ch == '.' || ch == 'e' || ch == '-' || ch=='+')) {
								if(ch == '.' || ch == 'e') {
									 isfloat = true;
								}
								state=digitGetNextStrustate(state,ch);
								if (state > 6) break;
								else
									token += ch; 
								//列计数加1
								i++;
								if(i>=strline.length) break;
								ch = strline[i];
							}
							Boolean haveMistake = false;
							if (state == 2 || state == 4 || state == 5 || state == 7) 
	                        {  
	                            haveMistake = true;  
	                        } 
							//错误处理
							if (haveMistake) {
								//一直到“可分割”的字符结束  
	                        	while (ch != '\0' && ch != ',' && ch != ';' && ch != ' ')
	                            {  
	                                token += ch;  
	                                i++;
	                                if(i >= strline.length) break;  
	                                ch = strline[i];  
	                            }  
	                        	out2.write(token + "\t请确认无符号常数输入正确\n");
                                System.out.println(token + "\t请确认无符号常数输入正确");
							}else 
	                        {  
	                            if (isfloat) 
	                            {  
	                            	out.write(token+"\t<3, "+token+" >\n");
									System.out.println(token+"\t<3, "+token+" >");// 浮点型常量  
	                            } 
	                            else
	                            {  
	                            	out.write(token+"\t<2, "+token+" >\n");
									System.out.println(token+"\t<2, "+token+" >");//整形常量
	                            } 
	                        }
							i--;
							token = "";
						}
						//识别运算符
						else if(isOp(ch)){
							token += ch;
							//后面可以用一个"="
							if (isPlusEqu(ch)) {
								i++;
								if(i>=strline.length) break;
								 ch = strline[i];
								 if (ch == '=')
									 token += ch; 
								 else 
								//判断该符号后面是不是可以连着和自己一样的
								 {
									 if (isPlusSame(strline[i - 1]) && ch == strline[i - 1]) {
										 token += ch;										 
									 }else 
										 --i;
								 }
							}
						   //界符，赋值语句里的等于
						   if(token.equals("=")) {
							   int pos=isMatchOP(token)+45;
							  out.write(token+"\t<"+pos+", "+" _ >\n");
							  System.out.println(token+"\t<"+pos+", "+token+" >");
						   }
						   //运算符
						   else{
							  int pos=isMatchOP(token)+20;
							  out.write(token+"\t<"+pos+", "+" _ >\n");
							  System.out.println(token+"\t<"+pos+", "+token+" >");
						    }
						   token = "";
						}
						//识别界符
						else if(isBound(ch)) {
							token +=ch;
							int pos=isMatchOP(token)+45;
							  out.write(token+"\t<"+pos+", _ >\n");
							  System.out.println(token+"\t<"+pos+", "+token+" >");
							  token="";
						}
						//识别注释
						else if (ch == '/') {
							token += ch;
							i++;
							if(i>=strline.length) break;
							 ch = strline[i];
							 if (ch != '*') {
								 //此时识别到的/当作运算符中的除法来处理
								 if (ch == '=') {
									 token += ch; // /=
								 }
								 else 
		                         {  
									 --i; // / 列计数减1
		                         }
								 int pos=isMatchOP(token)+20;
								  out.write(token+"\t<"+pos+", "+" _ >\n");
								  System.out.println(token+"\t<"+pos+", "+token+" >");
							 }
							 //否则识别到/*
							 else {
								 Boolean haveMistake = false;
								 
							 }
							 
						}
					}					
		//		}
		//	}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
		
	}
}
