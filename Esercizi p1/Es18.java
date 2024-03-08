public class Es18 {
	public static boolean scan(String s) {
		int state = 0;
		int i = 0;

		while (state >= 0 && i < s.length()) {
			final char ch = s.charAt(i++);

			switch (state) {
				case 0:
					if (ch == '.')
						state = 2;
					else if (ch == '+' || ch == '-')
						state = 8;
					else if (ch >= 48 && ch <= 57)
						state = 1;
					else
						state = -1;
					break;

				case 1:
					if (ch == '.')
						state = 2;
					else if (ch >= 48 && ch <= 57)
						state = 1;
					else if (ch == 'e')
						state = 4;
					else
						state = -1;
					break;

				case 2:
					if (ch >= 48 && ch <= 57)
						state = 3;
					else
						state = -1;
					break;

				case 3:
					if (ch == 'e')
						state = 4;
					else if (ch >= 48 && ch <= 57)
						state = 3;
					else
						state = -1;
					break;

				case 4:
					if (ch == '.')
						state = 6;
					else if (ch == '+' || ch == '-')
						state = 9;
					else if (ch >= 48 && ch <= 57)
						state = 5;
					else
						state = -1;
					break;

				case 5:
					if (ch == '.')
						state = 6;
					else if (ch >= 48 && ch <= 57)
						state = 5;
					else
						state = -1;
					break;

				case 6:
					if (ch >= 48 && ch <= 57)
						state = 7;
					else
						state = -1;
					break;

				case 7:
					if (ch >= 48 && ch <= 57)
						state = 7;
					else
						state = -1;
					break;

				case 8:
					if (ch >= 48 && ch <= 57)
						state = 1;
					else if (ch == '.')
						state = 2;
					else
						state = -1;
					break;

				case 9:
					if (ch >= 48 && ch <= 57)
						state = 5;
					else if (ch == '.')
						state = 6;
					else
						state = -1;
					break;

			}
		}
		if (state == 1 || state == 3 || state == 5 || state == 7) {
			return true;
		} else {
			return false;
		}
	}

	public static void main(String[] args) {
		System.out.println(scan(args[0]) ? "OK" : "NOPE");
	}
}