
public class Esercizio4 {

    public static boolean scan(String s) {
        System.out.println((int)'"');
        int state = 0;
        int i = 0;
        while (state >= 0 && i < s.length()) {
            final char ch = s.charAt(i++);
            switch (state) {
                case 0:
                    if ((ch >=48 && ch<=57) && ch%2==0) {
                        state = 2;
                    } else if ((ch >=48 && ch<=57) && ch%2==1) {
                        state = 1;
                    }else if (ch==' ') {
                        state = 0;
                    } else if ((ch >=97 && ch<=122)||(ch >=65 && ch<=90)) {
                        state = 7;
                    }else {
                        state = -1;
                    }
                    break;
                case 1:
                    if ((ch >=48 && ch<=57) && ch%2==1) {
                        state = 1;
                    }else if ((ch >=48 && ch<=57) && ch%2==0) {
                        state = 2;
                    }else if (ch==' ') {
                        state = 3;
                    }else if ((ch >=76 && ch<=90)|| (ch >=108 && ch<=122)) {
                        state = 5;
                    }else if ((ch >=65 && ch<=75) || (ch >=97 && ch<=107)) {
                        state = 7;
                    } else {
                        state = -1;
                    }
                    break;
                case 2:
                    if ((ch >=48 && ch<=57) && ch%2==0) {
                        state = 2;
                    }else if ((ch >=48 && ch<=57) && ch%2==1) {
                        state = 1;
                    }else if (ch==' ') {
                        state = 4;
                    }else if ((ch >=65 && ch<=75) || (ch >=97 && ch<=107)) {
                        state = 5;
                    }else if ((ch >=76 && ch<=90) || (ch >=108 && ch<=122)) {
                        state = 7;
                    } else {
                        state = -1;
                    }
                    break;
                case 3:
                    if (ch==' ') {
                        state = 3;
                    }else if ((ch >=76 && ch<=90) || (ch >=108 && ch<=122)) {
                        state = 5;
                    }else if ((ch >=48 && ch<=57) || (ch >=65 && ch<=75) || (ch >=97 && ch<=107)) {
                        state = 7;
                    } else {
                        state = -1;
                    }
                    break;
                case 4:
                    if (ch==' ') {
                        state = 4;
                    }else if ((ch >=65 && ch<=75) || (ch >=97 && ch<=107)) {
                        state = 5;
                    }else if ((ch >=48 && ch<=57) || (ch >=76 && ch<=90) || (ch >=108 && ch<=122)) {
                        state = 7;
                    } else {
                        state = -1;
                    }
                    break;
                case 5:
                    if ((ch >=65 && ch<=90)||(ch >=97 && ch<=122)) {
                        state = 5;
                    }else if (ch==' ') {
                        state = 6;
                    }else if (ch >=48 && ch<=57) {
                        state = 4;
                    } else {
                        state = -1;
                    }
                    break;
                case 6:
                    if (ch==' ') {
                        state = 6;
                    }else if ((ch >=65 && ch<=90)||(ch >=97 && ch<=122) || (ch >=48 && ch<=57)) {
                        state = 7;
                    } else {
                        state = -1;
                    }
                    break;
                    
                case 7:
                    if ((ch >=65 && ch<=90)||(ch >=97 && ch<=122) || (ch >=48 && ch<=57) || ch==' ') {
                        state = 7;
                    }else {
                        state = -1;
                    }
                    break;
                    
            }
        }
        return state==5 || state==6;
    }

    public static void main(String[] args) {
        System.out.println(scan(args[0]) ? "OK" : "NOPE");
    }
}
