import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SemanticNetwork {
	static Set<String> network = new HashSet<String>();
	static Map<String, String> isaMap = new HashMap<String, String>();
	static Map<String, String> akoMap = new HashMap<String, String>();
	static Map<String, String> shouldAvoidMap = new HashMap<String, String>();
	static Map<String, String> containsMap = new HashMap<String, String>();
	static Set<String> allElements = new HashSet<String>();

	public static void main(String[] args) {
		network.add("davidisadiabetics");
		network.add("diabeticsshouldAvoidsugar");
		network.add("candycontainssugar");
		network.add("snickersakocandy");

		isaMap.put("david", "diabetics");
		akoMap.put("snickers", "candy");
		shouldAvoidMap.put("diabetics", "sugar");
		containsMap.put("candy", "sugar");

		allElements.add("david");
		allElements.add("diabetics");
		allElements.add("sugar");
		allElements.add("candy");
		allElements.add("snickers");

		System.out.println("How to Run the Program : ");
		System.out.println(
				"Allowed words (Case sensitive) : david, diabetics, sugar, candy, \nsnickers, isa, shouldAvoid, contains, ako");
		System.out.println(
				"There are two types of input allowed : \nType 1. No variables [format : david shouldAvoid candy] \nand \nType 2. With one variable X  [format : david shouldAvoid X]*\n*X can be anywhere");
		System.out.println("___________________________________________________________\n");

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			try {
				System.out.println("\nInput Type ? (1 or 2)");

				String in = br.readLine();
				if (in.equals("1")) {
					System.out.println("Enter type 1 input : 3 words space separated and return(Enter)\n");
					String input = br.readLine();
					String[] inputs = input.split(" ");
					if (inputs == null || inputs.length != 3) {
						System.out.println("Invalid Input : Re-run\n");
						System.exit(0);
					} else {
						if (value(inputs[0], inputs[1], inputs[2])) {
							System.out.println("YES");
						} else {
							System.out.println("NO");
						}
					}
				} else if (in.equals("2")) {
					System.out
							.println("Enter type 2 input : 3 words space separated with one \"X\" and return(Enter)\n");
					String input = br.readLine();
					String[] inputs = input.split(" ");
					if (inputs == null || inputs.length != 3) {
						System.out.println("Invalid Input : Re-run\n");
						System.exit(0);
					} else {
						if (inputs[0].equals("X")) {
							Set<String> missing = null;
							missing = firstMissing(missing, inputs[1], inputs[2]);
							System.out.println(missing.toString());
						} else if (inputs[1].equals("X")) {
							Set<String> missing = null;
							missing = secondMissing(missing, inputs[0], inputs[2]);
							System.out.println(missing.toString());
						} else if (inputs[2].equals("X")) {
							Set<String> missing = null;
							missing = thirdMissing(missing, inputs[0], inputs[1]);
							System.out.println(missing.toString());
						} else {
							System.out.println("Invalid Input : X expected : Re-run\n");
							System.exit(0);
						}

					}
				} else {
					System.out.println("Invalid Input : Re-run\n");
					System.exit(0);
				}
			} catch (Exception e) {
			}
		}
	}

	static boolean edge(String a, String b, String c) {
		if (network.contains(a + b + c)) {
			return true;
		} else {
			return false;
		}
	}

	static boolean value(String a, String b, String c) {
		if (a == null || b == null || c == null) {
			return false;
		}
		if ((edge(a, b, c)) || (value(isaMap.get(a), b, c)) || (value(akoMap.get(a), b, c))
				|| (value(a, b, akoMap.get(c)))
				|| (b.equals("shouldAvoid")
						&& (value(c, "contains", containsMap.get(c)) && (value(a, "shouldAvoid", shouldAvoidMap.get(a))
								&& (containsMap.get(c).equals(shouldAvoidMap.get(a))))))) {
			return true;
		} else {
			return false;
		}
	}

	static Set<String> firstMissing(Set<String> missing, String b, String c) {
		if (missing == null) {
			missing = new HashSet<String>();
		}
		if (b == null || c == null) {
			return missing;
		}
		if (b.equals("isa")) {
			for (String key : isaMap.keySet()) {
				if (isaMap.get(key).equals(c)) {
					missing.add(key);
				}
			}
			return missing;
		}
		if (b.equals("contains")) {
			for (String key : containsMap.keySet()) {
				if (containsMap.get(key).equals(c)) {
					missing.add(key);
					firstMissing(missing, "ako", key);
				}
			}
			return missing;
		}
		if (b.equals("ako")) {
			for (String key : akoMap.keySet()) {
				if (akoMap.get(key).equals(c)) {
					missing.add(key);
				}
			}
			return missing;

		}
		if (b.equals("shouldAvoid")) {
			for (String key : shouldAvoidMap.keySet()) {
				if (shouldAvoidMap.get(key).equals(c)) {
					missing.add(key);
					firstMissing(missing, "isa", key);
				}
			}
			firstMissing(missing, b, containsMap.get(c));
			firstMissing(missing, b, akoMap.get(c));
			return missing;
		}
		return missing;
	}

	static Set<String> secondMissing(Set<String> missing, String a, String c) {
		if (missing == null) {
			missing = new HashSet<String>();
		}
		if (a == null || c == null) {
			return missing;
		}
		Set<String> isaSet = null;
		isaSet = firstMissing(isaSet, "isa", c);
		if (isaSet != null && isaSet.contains(a)) {

			missing.add("ISA");
		}
		if (isaSet != null) {
			isaSet.clear();
		}

		isaSet = firstMissing(isaSet, "shouldAvoid", c);
		if (isaSet != null && isaSet.contains(a)) {

			missing.add("SHOULDAVOID");
		}
		if (isaSet != null) {
			isaSet.clear();
		}

		isaSet = firstMissing(isaSet, "contains", c);
		if (isaSet != null && isaSet.contains(a)) {

			missing.add("CONTAINS");
		}
		if (isaSet != null) {
			isaSet.clear();
		}

		isaSet = firstMissing(isaSet, "ako", c);
		if (isaSet != null && isaSet.contains(a)) {

			missing.add("AKO");
		}
		if (isaSet != null) {
			isaSet.clear();
		}
		return missing;
	}

	static Set<String> thirdMissing(Set<String> missing, String a, String b) {
		if (missing == null) {
			missing = new HashSet<String>();
		}
		for (String element : allElements) {
			Set<String> elementSet = firstMissing(null, b, element);
			if (elementSet.contains(a)) {
				missing.add(element);
			}
		}
		return missing;
	}
}
