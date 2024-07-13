  
public class Esercizio8 {

    public static boolean scan(String s) {
        int state = 0;
        int i = 0;
        while (state >= 0 && i < s.length()) {
            final char ch = s.charAt(i++);
            switch (state) {
                case 0:
                    if (ch=='D' || ch=='d') {
                        state = 1;
                    } else if (ch>32 && ch<127) {
                        state = 7;
                    } else {
                        state = -1;
                    }
                    break;
                case 1:
                    if (ch=='a') {
                        state = 2;
                    } else if (ch>32 && ch<127) {
                        state = 8;
                    } else {
                        state = -1;
                    }
                    break;
                case 2:
                    if (ch=='n') {
                        state = 3;
                    } else if (ch>32 && ch<127) {
                        state = 9;
                    } else {
                        state = -1;
                    }
                    break;
                case 3:
                    if (ch=='i') {
                        state = 4;
                    } else if (ch>32 && ch<127) {
                        state = 10;
                    } else {
                        state = -1;
                    }
                    break;
                case 4:
                    if (ch=='e') {
                        state = 5;
                    } else if (ch>32 && ch<127) {
                        state = 11;
                    } else {
                        state = -1;
                    }
                    break;
                case 5:
                    if (ch>32 && ch<127) {
                        state = 6;
                    } else {
                        state = -1;
                    }
                    break;
                case 6:
                    if (ch>32 && ch<127) {
                        state = 12;
                    } else {
                        state = -1;
                    }
                    break;
                case 7:
                    if (ch=='a') {
                        state = 8;
                    } else if (ch>32 && ch<127) {
                        state = 12;
                    } else {
                        state = -1;
                    }
                    break;
                case 8:
                    if (ch=='n') {
                        state = 9;
                    } else if (ch>32 && ch<127) {
                        state = 12;
                    } else {
                        state = -1;
                    }
                    break;
                case 9:
                    if (ch=='i') {
                        state = 10;
                    } else if (ch>32 && ch<127) {
                        state = 12;
                    } else {
                        state = -1;
                    }
                    break;
                case 10:
                    if (ch=='e') {
                        state = 11;
                    } else if (ch>32 && ch<127) {
                        state = 12;
                    } else {
                        state = -1;
                    }
                    break;
                case 11:
                    if (ch=='l') {
                        state = 6;
                    } else if (ch>32 && ch<127) {
                        state = 12;
                    } else {
                        state = -1;
                    }
                    break;
                case 12:
                    if (ch>32 && ch<127) {
                        state = 12;
                    } else {
                        state = -1;
                    }
                    break;
                
                    
            }
        }
        return state==6;
    }

    public static void main(String[] args) {
        System.out.println(scan(args[0]) ? "OK" : "NOPE");
    }
}