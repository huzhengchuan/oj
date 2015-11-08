package huawei;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;

public class Rule {

	/**
	 * 功能:请实现以下函数，该函数事先规则匹配
	 * @param rule 规格
	 * @param map 规则中涉及的指标数据
	 * @return true: 匹配成功，false:匹配失败
	 * */
	public boolean fireRule(String rule, Map<String, Integer>map)
	{
		/* 除去无效的空格，简化算法的处理过程 */
		rule = rule.replaceAll("\\s*", "");
		
		/* 根据规则进行分词处理 */
		Vector<String>vectors = parseInput(rule);
		
		/* 分词之后进行规则判断 */
		boolean isValid = isValidVectors(vectors);
		if(isValid == false)
		{
			return false;
		}
		
		/*将中缀表达式转化成后缀表达式*/
		Vector<String>result = infix_exp_value(vectors);
		
		/*检查后缀表达式的合法性*/
		if(isValidInfix(result) == false)
		{
			return false;
		}
		System.out.println("");
		return compute(result, map);
	}
	
	public boolean isValidInfix(Vector<String> vectors)
	{
		int loop = 0;
		while(loop < vectors.size())
		{
			if(vectors.get(loop).equals("(") || vectors.get(loop).equals(")"))
			{
				return false;
			}
			loop++;
		}
		return true;
	}
	
	public boolean isValidVectors(Vector<String> vectors)
	{
		int loop = 0;
		if(vectors == null)
		{
			return false;
		}
		
		/*数字以及mem是否存在连接的情况*/
		while(loop < vectors.size() - 1)
		{
			if(isOperObj(vectors.get(loop)) && isNum(vectors.get(loop+1)))
			{
				return false;
			}
			if(isOperObj(vectors.get(loop + 1)) && isNum(vectors.get(loop)))
			{
				return false;
			}
			if(isOperObj(vectors.get(loop)) && isOperObj(vectors.get(loop+1)))
			{
				return false;
			}
			loop++;
		}
		
		return true;
	}
	public boolean isIncludeWord(String subStr)
	{
		List<String> paraList = new ArrayList<String>();
		paraList.add("("); paraList.add(")");
		paraList.add(">="); paraList.add("<=");
		paraList.add("=="); paraList.add(">");paraList.add("||"); 
		paraList.add("<"); paraList.add("&&");
		paraList.add("cpu");paraList.add("mem");paraList.add("store");
		
		Iterator<String> it = paraList.iterator();
		while(it.hasNext())
		{
			if(subStr.equals((String)it.next()) == true)
			{
				return true;
			}
		}
		try{
			Integer.parseInt(subStr);
		}catch(Exception e)
		{
			return false;
		}
		return true;
	}
	
	public Vector<String> parseInput(String inputStr)
	{
		Vector<String> parsedInput = new Vector<String>();
		int loop = 0;
		int sepFlag = 0;
		int stepStart = 0;
		
		sepFlag = 0;
		while(loop < inputStr.length())
		{
			String subStr = inputStr.substring(stepStart, loop + 1);
			if(isIncludeWord(subStr))
			{
				/*单词的组合,贪婪匹配*/
				sepFlag = 1;
				loop++;
				continue;
			}
			
			if(sepFlag == 1)
			{
				/*之前找到最大的子串*/ 
				String wordStr = inputStr.substring(stepStart, loop);
				System.out.println(wordStr);
				parsedInput.add(wordStr);
				stepStart = loop;
				sepFlag = 0;
				continue;
			}
			else
			{
				/*之前没有找到到最大子串，追加子串继续寻找*/
				loop++; 
			}
		}
		
		if(loop == inputStr.length() && isIncludeWord(inputStr.substring(stepStart, loop)))
		{
			String wordStr = inputStr.substring(stepStart, loop);
			System.out.println(wordStr);
			parsedInput.add(wordStr);
			return parsedInput;
		}
		return null;
	}
	
	public int priority(String oper)
	{
		if(oper.equals("#"))
		{
			return 1;
		}
		if(oper.equals(")"))
		{
			return 2;
		}
		if(oper.equals("&&") || oper.equals("||"))
		{
			return 3;
		}
		if(oper.equals(">=") || oper.equals("<=") || oper.equals(">") || oper.equals("<") || oper.equals("=="))
		{
			return 4;
		}
		if(oper.equals("("))
		{
			return 5;
		}
		return 0;
	}
	
	public boolean isOper(String str)
	{
		if(str.equals("(") || str.equals(")") || str.equals(">=") ||
				str.equals("<=") || str.equals("<") || str.equals(">") ||
				str.equals("==") || str.equals("||") || str.equals("&&"))
		{
			return true;
		}
		return false;
	}
	
	public boolean isOperObj(String str)
	{
		if(str.equals("mem") || str.equals("cpu") || str.equals("store"))
		{
			return true;
		}
		return false;
	}
	
	public boolean isNum(String str)
	{
		try{
			Integer.parseInt(str);
		}catch(Exception e)
		{
			return false;
		}
		return true;
	}
	
	/*将输入的中缀表达式转化成后缀表达式*/
	public Vector<String> infix_exp_value(Vector<String> inputVectors)
	{
		Stack<String> stack = new Stack<String>();
		Vector<String> outputVectors = new Vector<String>();
		int loop = 0;
		
		stack.push("#");
		
		while(loop < inputVectors.size())
		{
			String subStr = inputVectors.get(loop);
			String topStr = stack.peek();
			
			System.out.println("oper-"+subStr);
			if(isNum(subStr) || isOperObj(subStr))
			{
				outputVectors.add(subStr);
				loop++;
				System.out.println("\t\toutput-"+subStr);
				continue;
			}
			
			if(topStr.equals("("))
			{
				if(subStr.equals(")"))
				{
					stack.pop();
				}
				else
				{
					System.out.println("\t\tinstack-"+subStr);
					stack.push(subStr);
				}
				loop++;
				continue;
			}
			
			if(priority(topStr) < priority(subStr))
			{
				stack.push(subStr);
				System.out.println("\t\tinstack-"+subStr);
				loop++;
				continue;
			}
			else
			{
				System.out.println("\t\toutput-"+stack.peek());
				outputVectors.add(stack.pop());
				
				continue;
			}
			
			
		}
		
		while(!stack.empty())
		{
			System.out.println("\t\toutput-"+stack.peek());
			if(! stack.peek().equals("#"))
			{
				outputVectors.add(stack.pop());
			}
			else
			{
				stack.pop();
			}
		}
		
		
		return outputVectors;
	}
	
	public Integer computeMeta(String oper, Integer para1, Integer para2)
	{
		boolean result = false;
		if(oper.equals("<") || oper.equals(">") || oper.equals("<=") || oper.equals(">=") || oper.equals("=="))
		{
		
			if(oper.equals("<"))
			{
				result = para1 < para2;
			}
			if(oper.equals(">"))
			{
				result = para1 > para2;
			}
			if(oper.equals("<="))
			{
				result = para1 <= para2;
			}
			if(oper.equals(">="))
			{
				result = para1 >= para2;
			}
			if(result == false)
			{
				return 0;
			}else{
				return 1;
			}
			
		}
		
		if(oper.equals("||") || oper.equals("&&"))
		{
			if(oper.equals("||"))
			{
				return para1.intValue() + para2.intValue();
			}
			if(oper.equals("&&"))
			{
				return para1.intValue() * para2.intValue();
			}
		}
		
		return 0;
		
	}
	
	public boolean compute(Vector<String>input, Map<String, Integer>map)
	{
		int loop = 0;
		Stack<Integer> stack = new Stack<Integer>();
		
		while(loop < input.size())
		{
			String subStr = input.get(loop);
			if(isOperObj(subStr))
			{
				System.out.println(map.get(subStr) + subStr);
				if(map.get(subStr) == null)
				{
					return false;
				}
				stack.push(map.get(subStr));
				loop++;
				continue;
			}
			
			if(isNum(subStr))
			{
				System.out.println(Integer.parseInt(subStr)+subStr);
				stack.push(Integer.parseInt(subStr));
				loop++;
				continue;
			}
			
			if(isOper(subStr))
			{
				Integer para1 = stack.pop();
				Integer para2 = stack.pop();
				System.out.print("" + para2 + subStr + para1 );
				Integer str = computeMeta(subStr, para2, para1);
				System.out.print("="+str + "\n");
				stack.push(str);
				loop++;
				continue;
			}
			
		}
		if(stack.peek().equals(1))
		{  
			return true;
		}else{
			return false;
		}

	}
	
	/**************测试用例**************/
	public static void main(String[] args) 
	{
		String ruleStr = "(cpu>=10&&mem>=100)||store>=0";
		Rule rule = new Rule();
		Map<String, Integer>map = new HashMap<String, Integer>();
		map.put("cpu", 80);
		map.put("mem", 89);
		map.put("store", 10);
		System.out.println("The result="+rule.fireRule(ruleStr, map));
	}
	
	
}
