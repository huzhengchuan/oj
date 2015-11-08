package testcase;

import huawei.Rule;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

public class RuleTest {
	
	public void testCase01()
	{
		Rule rule = new Rule();
		String ruleStr = "cpua>=10&&mem>=100";
		Map<String, Integer>map = new HashMap<String, Integer>();
		map.put("cpu", 10);
		map.put("mem", 80);
		rule.fireRule(ruleStr, map);
		//Assert.assertEquals(false, rule.fireRule(ruleStr, map));
	}
}
