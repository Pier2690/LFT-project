public class Es17 {
	public static boolean scan(String s) {
		int state = 0;
		int i = 0;

		while (state >= 0 && i < s.length()) {
			final char ch = s.charAt(i++);

			switch (state) {
				case 0:
					if (ch == 'P')
						state = 1;
					else if (ch >= 0 && ch <= 79 || ch >= 81 && ch <= 126)
						state = 5;
					else
						state = -1;
					break;

				case 1:
					if (ch == 'i')
						state = 2;
					else if (ch >= 0 && ch <= 104 || ch >= 106 && ch <= 126)
						state = 6;
					else
						state = -1;
					break;

				case 2:
					if (ch == 'e')
						state = 3;
					else if (ch >= 0 && ch <= 100 || ch >= 102 && ch <= 126)
						state = 7;
					else
						state = -1;
					break;

				case 3:
					if (ch == 'r')
						state = 4;
					else if (ch >= 0 && ch <= 113 || ch >= 115 && ch <= 126)
						state = 8;
					else
						state = -1;
					break;

				case 4:
					if (ch == 32)
						state = 4;
					else
						state = -1;
					break;

				case 5:
					if (ch == 'i')
						state = 6;
					else
						state = -1;
					break;

				case 6:
					if (ch == 'e')
						state = 7;
					else
						state = -1;
					break;

				case 7:
					if (ch == 'r')
						state = 8;
					else
						state = -1;
					break;

				case 8:
					if (ch == 32)
						state = 8;
					else
						state = -1;
					break;
			}
		}
		if (state == 4 || state == 8) {
			return true;
		} else {
			return false;
		}
	}

	public static void main(String[] args) {
		System.out.println(scan(args[0]) ? "OK" : "NOPE");
	}
}