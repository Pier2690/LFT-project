public class Es16 {
	public static boolean scan(String s) {
		int state = 0;
		int i = 0;

		while (state >= 0 && i < s.length()) {
			final char ch = s.charAt(i++);

			switch (state) {
				case 0:
					if (ch == 'b')
						state = 0;
					else if (ch == 'a')
						state = 1;
					else
						state = -1;
					break;

				case 1:
					if (ch == 'a')
						state = 1;
					else if (ch == 'b')
						state = 2;
					else
						state = -1;
					break;

				case 2:
					if (ch == 'b')
						state = 3;
					else if (ch == 'a')
						state = 1;
					else
						state = -1;
					break;

				case 3:
					if (ch == 'b')
						state = 0;
					else if (ch == 'a')
						state = 1;
					else
						state = -1;
					break;
			}

		}
		if (state == 1 || state == 2 || state == 3) {
			return true;
		} else {
			return false;
		}

	}

	public static void main(String[] args) {
		System.out.println(scan(args[0]) ? "OK" : "NOPE");
	}
}