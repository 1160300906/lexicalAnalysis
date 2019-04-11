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
	//�ؼ��֣��ֱ����6��ʼ
	public static String keywords[] = {"int","float","double", "if","else", "do", "while", 
			"continue","break","typedef","struct","const","char","static"};
//	public static String keywordstoken[] = { "INT","FLOAT", "DOUBLE", "IF","ELSE","DO","WHILE",
//			"CONTINUE","BREAK","TYPEDEF","STRUCT","CONST","CHAR","STATIC"};
	//�����
	public static char operator[] = { '+', '-', '*', '=', '<', '>', '&', '|', '~',  
	         '^', '!', '%'};
	//�ֱ��룺20��ʼ
	public static String operatortoken[] = {"+","-","*","/","<",">","&","|","~","^","!",
			"%","++","--",">>","<<","&&","||","!=",">=","<=","+=","-=","*=","/="};
	//�������ɼӵ���
	public static Boolean isPlusEqu(char ch)   
	{  
	     return ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '='  
	            || ch == '>' || ch == '<' || ch == '&' || ch == '|'  
	            || ch == '^' || ch == '!';  
	 }
	//�����������������һ��
	public static Boolean isPlusSame(char ch) {
		 return ch == '+' || ch == '-' || ch == '&' || ch == '|' || ch == '<' || ch == '>';  
	}
	//������ֱ���45��ʼ
	public static char boundary[] = { '=', ',', ';', '[', ']', '(', ')', '{', '}'};
	public static String boundarytoken[] = { "=", ",", ";", "[", "]", "(", ")", "{", "}"};
	//�ж���ĸ���»���
	public static Boolean isAlpha(char ch)
	{
		return ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || ch == '_');
	}
	//�ж�����
	public static Boolean isDigit(char ch)
	{  
        return (ch >= '0' && ch <= '9');  
    }
	//�ж��Ƿ�������� 
	public static Boolean isOp(char ch) 
	{  
	      for (int i = 0; i < operator.length; i++) {
	    	 if (ch == operator[i]) {  
	               return true;  
	           }   
	      }	       
	        return false;  
	 }
	//�ж��Ƿ��ǽ������=���Ѿ���������ﴦ����ˣ��������ڲ����ǵȺ�
	public static Boolean isBound(char ch) {
		 for (int i = 0; i < boundary.length; i++) {
			 if (ch == boundary[i] && ch!='=') {  
		               return true;  
		        }   
		 }      
		        return false;  
	}
	//�жϹؼ���,���ظùؼ����ڹؼ��������е�λ��
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
	//�ж��������������������е�λ��
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
	//�ж��������������������е�λ��
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
	//���ֳ���DFA�����һ��״̬
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
			break;
		case 2:
			if(isDigit(ch))
				nextstate =3;
			break;
		case 3:
			if(isDigit(ch))
				nextstate =3;
			else if(ch == 'e')
				nextstate=4;
			break;
		case 4:
			if(ch == '-' || ch == '+' || isDigit(ch))
				nextstate=5;
			break;
		case 5:
			if(isDigit(ch))
				nextstate =6;
			break;
		case 6:
			if(isDigit(ch))
				nextstate =6;		
			break;
		}
		return nextstate;
	}
	//ע�͵�DFA�����һ��״̬
	public static int noteGetNextStrustate(int startstate,char ch) {
		int nextstate=-1;
		switch (startstate) {
		case 1:
			if(ch=='*')
				nextstate=2;
			else 
				nextstate=1;
			break;
		case 2:
			if(ch=='/')
				nextstate=3;
			else if(ch=='*')
				nextstate=2;
			else 
				nextstate=1;
			break;
		}
		return nextstate;
	}
	//���ļ�
		public static ArrayList<String> readfile(File file){
			ArrayList<String> strings=new ArrayList<String>();
		       try{
		           BufferedReader br = new BufferedReader(new FileReader(file));//����һ��BufferedReader������ȡ�ļ�
		           String s = null;
		            while((s = br.readLine())!=null){//ʹ��readLine������һ�ζ�һ��
		               strings.add(s);
		            }
		            br.close();    
		        }catch(Exception e){
		            e.printStackTrace();
		        }
		       return strings;
		   }
	public static void main(String[] args) {
		String filepath="src/lexicalAnalysis/input.txt";
		File file=new File(filepath);
		symbol.clear();
		ArrayList<String> texts=new ArrayList<String>();
		int stack1=0;//�����ж�'('�Ƿ���
		int stack2=0;//�����ж�'['�Ƿ���
		int stack3=0;//�����ж�'{'�Ƿ���
		texts=readfile(file);
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter("src/lexicalAnalysis/token.txt"));
			BufferedWriter out1 = new BufferedWriter(new FileWriter("src/lexicalAnalysis/symbol.txt"));
			BufferedWriter out2 = new BufferedWriter(new FileWriter("src/lexicalAnalysis/error.txt"));
            //symbol.txt�Ƿ��ű�
			String str = "";
			for(int j = 0; j < texts.size(); j++) {
				str=str+texts.get(j);
			}
					char[] strline = str.toCharArray();
					for(int i= 0; i < strline.length; i++) {
						//����strline�е�ÿ���ַ�
						char ch = strline[i];
						//��ʼ��token�ַ���Ϊ��
						String token = "";
						//�жϱ�ʶ�͹ؼ���
						if (isAlpha(ch)) {
							do {  
	                            token += ch;  
	                            i++;  
	                            if(i >= strline.length) break;  
	                            ch = strline[i];  
	                        } while (ch != '\0' && (isAlpha(ch) || isDigit(ch))); 
							--i;//ָ����ˣ��м�����1
							//�ǹؼ���
							int pos=isMatchKeyword(token.toString());
							if(pos!=-1) {
								//������ű�Ϊ�ջ���ű��в�������ǰtoken�������
								if (symbol.isEmpty() || (!symbol.isEmpty() && !symbol.containsKey(token))) 
	                        	{  
	                                symbol.put(token, symbol_pos);  
	                                out1.write(token+"\t"+symbol_pos+"\n");
	                                symbol_pos++;
	                            }
								out.write(token+"\t<"+(pos+6)+", _ >\n");
								System.out.println(token+"\t<"+(pos+6)+", _ >");
							}
							//�Ǳ�ʶ��,��ʶ�����ֱ���Ϊ1
							else {
								//������ű�Ϊ�ջ���ű��в�������ǰtoken�������
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
						//�ж����ֳ���
						else if(isDigit(ch)) {
							//��ʼ������1״̬
							int state = 1;
							Boolean isfloat = false;
							token += ch; 
							i++;
							if(i>=strline.length) break;
							ch = strline[i];//  
							while ((ch != '\0') &&(isDigit(ch) || ch == '.' || ch == 'e' || ch == '-' || ch=='+')) {
								if(ch == '.' || ch == 'e') {
									 isfloat = true;
								}
								state=digitGetNextStrustate(state,ch);
								System.out.println(state);
								if (state > 6) break;
								else
									token += ch; 
								//�м�����1
								i++;
								if(i>=strline.length) break;
								ch = strline[i];
							}
							Boolean haveMistake = false;
							if (state == 2 || state == 4 || state == 5 || state == 7) 
	                        {  
	                            haveMistake = true;  
	                        } 
							//������
							if (haveMistake) {
								//һֱ�����ɷָ���ַ�����  
	                        	while (ch != '\0' && !isBound(ch) && ch != ' ')
	                            {  
	                                token += ch;  
	                                i++;
	                                if(i >= strline.length) break;  
	                                ch = strline[i];  
	                            }  
	                        	out2.write(token + "\t��ȷ���޷��ų���������ȷ\n");
                                System.out.println(token + "\t��ȷ���޷��ų���������ȷ");
							}else 
	                        {  
	                            if (isfloat) 
	                            {  
	                            	out.write(token+"\t<3, "+token+" >\n");
									System.out.println(token+"\t<3, "+token+" >");// �����ͳ���  
	                            } 
	                            else
	                            {  
	                            	out.write(token+"\t<2, "+token+" >\n");
									System.out.println(token+"\t<2, "+token+" >");//���γ���
	                            } 
	                        }
							i--;
							token = "";
						}
						//ʶ�������
						else if(isOp(ch)){
							token += ch;
							//���������һ��"="
							if (isPlusEqu(ch)) {
								i++;
								if(i>=strline.length) break;
								 ch = strline[i];
								 if (ch == '=')
									 token += ch; 
								 else 
								//�жϸ÷��ź����ǲ��ǿ������ź��Լ�һ����
								 {
									 if (isPlusSame(strline[i - 1]) && ch == strline[i - 1]) {
										 token += ch;										 
									 }else 
										 --i;
								 }
							}
						   //�������ֵ�����ĵ���
						   if(token.equals("=")) {
							   int pos=isMatchbound(token)+45;
							  out.write(token+"\t<"+pos+", "+" _ >\n");
							  System.out.println(token+"\t<"+pos+", "+token+" >");
						   }
						   //�����
						   else{
							  int pos=isMatchOP(token)+20;
							  out.write(token+"\t<"+pos+", "+" _ >\n");
							  System.out.println(token+"\t<"+pos+", "+token+" >");
						    }
						   token = "";
						}
						//ʶ����
						else if(isBound(ch)) {
							token +=ch;
							if(stack1<0) {
								stack1=0;
								out2.write(token + "\tû����֮ƥ���������\n");
                                System.out.println(token + "\tû����֮ƥ���������");
							}
							if(stack2<0) {
								stack2=0;
								out2.write(token + "\tû����֮ƥ���������\n");
                                System.out.println(token + "\tû����֮ƥ���������");
							}
							if(stack3<0) {
								stack3=0;
								out2.write(token + "\tû����֮ƥ���������\n");
                                System.out.println(token + "\tû����֮ƥ���������");
							}
							if(ch=='(')
								stack1++;
							else if(ch==')')
								stack1--;
							if(ch=='[')
								stack2++;
							else if(ch==']')
								stack2--;
							if(ch=='{')
								stack3++;
							else if(ch=='}')
								stack3--;
							int pos=isMatchbound(token)+45;
							  out.write(token+"\t<"+pos+", _ >\n");
							  System.out.println(token+"\t<"+pos+", "+token+" >");
							  token="";
						}
						//ʶ��ע��
						else if (ch == '/') {
							token += ch;
							i++;
							if(i>=strline.length) break;
							 ch = strline[i];
							 if (ch != '*') {
								 //��ʱʶ�𵽵�/����������еĳ���������
								 if (ch == '=') {
									 token += ch; // /=
								 }
								 else 
		                         {  
									 --i; // / �м�����1
		                         }
								 int pos=isMatchbound(token)+20;
								  out.write(token+"\t<"+pos+", "+" _ >\n");
								  System.out.println(token+"\t<"+pos+", "+token+" >");
							 }
							 //����ʶ��/*
							 else {
								 Boolean haveMistake = false;
								//��ʼ������1״̬
								int state = 1;
								i++;
								if(i>=strline.length) break;
								ch = strline[i];
								token="/*";
								while(true) {
									state=noteGetNextStrustate(state, ch);
									if(state==3) {
										token+=ch;
										break;
									}										
									else {
										i++;
									}
									token+=ch;
									if(i>=strline.length) break;
									ch = strline[i];
								}
								//���������ʱ��ע�͵��Զ�������1��2״̬�����д�˵��û�м�⵽*/
								if(state==1 || state==2 ) {
									haveMistake=true;
								}
								if(haveMistake) {
									out2.write(token + "\tע��/*�����\n");
	                                System.out.println(token + "\tע��/*�����");
								}
								else {
									out.write(token+"\t<"+55+", "+" _ >\n");//ע�͵��ֱ����Ϊ55
								  System.out.println(token+"\t<"+55+", "+token+" >");
								}
							 }
							 
						}
					}
					if(stack1!=0) {
						out2.write("(\t(�����\n");
                    System.out.println("(\t(�����\n");
					}
					if(stack2!=0) {
						out2.write("[\t[�����\n");
                    System.out.println("[\t[�����\n");
					}
					if(stack3!=0) {
						out2.write("[\t{�����\n");
                    System.out.println("[\t{�����\n");
					}					
				out.close();
				out1.close();
				out2.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
		
	}
}
