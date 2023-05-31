import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class Main {

    private static final int WIN_COUNT = 4;
    private static final char DOT_HUMAN = 'X';
    private static final char DOT_AI = 'O';
    private static final char DOT_EMPTY = '•';

    private static final Scanner SCANNER = new Scanner(System.in);

    private static int[] aiStep = new int[2];    // массив для хранения координат хода блокировки игрока компьютером

    private static String[] storageHorizonStepsAI;        // массив для хранения возможных ходов ИИ по горизонтали.
    private static String[] storageVerticalStepsAI;        // массив для хранения возможных ходов ИИ по вертикали.
    private static String[] storageDiagonalUpStepsAI;   // массив для хранения возможных ходов ИИ по диагонали слева на право.
    private static String[] storageDiagonalDownStepsAI;   // массив для хранения возможных ходов ИИ по диагонали слева на право.


    private static final int ALARM_NUMBER = 2;   // количество оставшихся до победы незаполненных ячеек, при которых включится в работу ИИ.
    private static char[][] field;

    private static final Random random = new Random();

    private static int fieldSizeX;
    private static int fieldSizeY;

    /**
     * Вспомогательные константы с путями для записи результата анализа ИИ
     */
    private static final String PATH_STEPS = "d://testFolders//GeekBrains//GB.Lesson_2.STEPS.txt";
    private static final String PATH_ANALISE = "d://testFolders//GeekBrains//GB.Lesson_2.ANALISE.txt";
    private static final String PATH_AI = "d://testFolders//GeekBrains//GB.Lesson_2.AI.txt";

    /**
     * Счетчик ходов
     */
    private static int countSteps;        // счетчик итераций цикла игры


    public static void main(String[] args) {
        while (true) {
            initialize();
            printField();

            while (true) {
                countSteps++;
                humanTurn();
                printField();
                if (gameCheck(DOT_HUMAN, "Вы победили!"))
                    break;
                aiTurn();
                printField();
                if (gameCheck(DOT_AI, "Компьютер победил!"))
                    break;
            }
            System.out.println("Желаете сыграть еще раз? (Y - да)");
            if (!SCANNER.next().equalsIgnoreCase("Y"))
                break;
        }

    }


    /**
     * Инициализация игрового поля
     */
    private static void initialize() {
        countSteps = 0;
        aiStep[0] = fieldSizeX;         // инициализируем `проверяемый` элемент

        fieldSizeX = 5;
        fieldSizeY = 5;
        storageHorizonStepsAI = new String[fieldSizeY * fieldSizeX];
        storageVerticalStepsAI = new String[fieldSizeY * fieldSizeX];
        storageDiagonalUpStepsAI = new String[fieldSizeY * fieldSizeX];
        storageDiagonalDownStepsAI = new String[fieldSizeY * fieldSizeX];

        field = new char[fieldSizeX][fieldSizeY];

        for (int x = 0; x < fieldSizeX; x++) {
            for (int y = 0; y < fieldSizeY; y++) {
                field[x][y] = DOT_EMPTY;
            }
        }
    }

    /**
     * Отрисовка игрового поля
     */
    private static void printField() {
        System.out.print("+");
        for (int i = 0; i < fieldSizeX * 2 + 1; i++) {
            System.out.print((i % 2 == 0) ? "-" : i / 2 + 1);
        }
        System.out.println();

        for (int i = 0; i < fieldSizeX; i++) {
            System.out.print(i + 1 + "|");

            for (int j = 0; j < fieldSizeY; j++)
                System.out.print(field[i][j] + "|");

            System.out.println();
        }

        for (int i = 0; i < fieldSizeX * 2 + 2; i++) {
            System.out.print("-");
        }
        System.out.println();

    }

    /**
     * Обработка хода игрока (человек)
     */
    private static void humanTurn() {
        int x, y;
        do {
            System.out.print("Введите координаты хода X и Y (от 1 до 5) через пробел >>> ");
            x = SCANNER.nextInt() - 1;
            y = SCANNER.nextInt() - 1;
        }
        while (!isCellValid(x, y) || !isCellEmpty(x, y));
        field[x][y] = DOT_HUMAN;
    }

    /**
     * Проверка, ячейка является пустой
     *
     * @param x
     * @param y
     * @return
     */
    static boolean isCellEmpty(int x, int y) {
        return field[x][y] == DOT_EMPTY;
    }


    /**
     * Проверка корректности ввода
     * (координаты хода не должны превышать размерность массива, игрового поля)
     *
     * @param x
     * @param y
     * @return
     */
    static boolean isCellValid(int x, int y) {
        return x >= 0 && x < fieldSizeX && y >= 0 && y < fieldSizeY;
    }


    /**
     * Ход компьютера
     * Логика блокировки хода пользователя основана на анализе компьютером недостающих меток
     * до полной выйгрышной комбинации пользователя.
     */
    private static void aiTurn() {
        int x, y;
        do {
            if (aiStep[0] != fieldSizeX ) {
                x = aiStep[0];
                y = aiStep[1];
                aiStep[0] = fieldSizeX; // возврат в 'режим ожидания'
            } else {
                x = random.nextInt(fieldSizeX);
                y = random.nextInt(fieldSizeY);
            }
        }
        while (!isCellEmpty(x, y));
        field[x][y] = DOT_AI;
    }

    /**
     * Проверка победы
     *
     * @param playerSign
     * @return
     */
    public static boolean checkWin(char playerSign) {

        for (int i = 0; i < fieldSizeX; i++) {
            for (int j = 0; j < fieldSizeY; j++) {
                if (field[i][j] == playerSign) {
                    int count = 1;

                    int countHorRight = checkInsideFieldWinHorRight(i, j, playerSign, count);
//                    String resultHorizRight = (countHorRight == WIN_COUNT) ? "+++ Победа по горизонтали" : "По горизонтали(вправо) не хватает до полной комбинации:" + (WIN_COUNT - countHorRight);
//                    System.out.println(resultHorizRight);
//                    String notionAI = playerSign + " field " + (i + 1) + " " + (j + 1 + (WIN_COUNT - ALARM_NUMBER));// ячейка, которую рекомендуется заполнить противоположной фишкой


                    int countVertBottom = checkInsideFieldWinVertBottom(i, j, playerSign, count);
//                    String resultVertBottom = (countVertBottom == WIN_COUNT) ? "+++ Победа по вертикали" : "По вертикали(вниз) не хватает до полной комбинации: " + (WIN_COUNT - countVertBottom);
//                    System.out.println(resultVertBottom);

                    int countDiagonalBottom = checkInsideFieldWinDiagonalBottom(i, j, playerSign, count);
//                    String resultDiagonalBottom = (countDiagonalBottom == WIN_COUNT) ? "+++ Победа по диагонали-вниз" : "По диагонали(вниз) не хватает до полной комбинации: " + (WIN_COUNT - countDiagonalBottom);
//                    System.out.println(resultDiagonalBottom);

                    int countDiagonalTop = checkInsideFieldWinDiagonalTop(i, j, playerSign, count);
//                    String resultDiagonalTop = (countDiagonalTop == WIN_COUNT) ? "+++ Победа по диагонали-вверх\n" : "По диагонали(вверх) не хватает до полной комбинации: " + (WIN_COUNT - countDiagonalTop);
//                    System.out.println(resultDiagonalTop);

                    analyzeAI(playerSign,countHorRight, countVertBottom,countDiagonalTop,i,j);

                    count = Math.max(Math.max(countHorRight, countVertBottom), Math.max(countDiagonalBottom, countDiagonalTop));

                    if (count >= WIN_COUNT) {
                        return true;
                    }


                }
            }
        }


        return false;
    }

    /**
     * Метод проверки игрового поля на наличие выйгрышной комбинации.
     * Проверка осуществляется от текущей ячейки вправо по строке.
     *
     * @param i     - первая координата(строка) текущей ячейки.
     * @param j     - вторая координата(колонка) текущей ячейки.
     * @param sign  - проверяемая текущая метка(фишка) пользователя\компьютера.
     * @param count - счётчик количества совпадений соответствующих текущей метке.
     * @return - возврат счётчика.
     */
    public static int checkInsideFieldWinHorRight(int i, int j, char sign, int count) {
//        String notionAI = sign + "field " + (i + 1) + " " + (j + 1);
//        writeToFile(notionAI, PATH_STEPS);

        try {
            if (field[i][j + 1] == sign) {
                count++;


                int horizon = checkInsideFieldWinHorRight(i, j + 1, sign, count);

                count = horizon;


                return count;
            } else {
//            count = 0; // НУЖЕН ЛИ??
                // запись возможного варианта для блокировки хода в файл
//                notionAI = sign + "field " + (i + 1) + " " + (j + 1);
//                writeToFile(notionAI, PATH_ANALISE);
            }

        } catch (RuntimeException e) {
            e.getMessage();
        }

        return count;

    }

    /**
     * @param i
     * @param j
     * @param sign
     * @param count
     * @return
     */
    public static int checkInsideFieldWinVertBottom(int i, int j, char sign, int count) {

        try {
            if (field[i + 1][j] == sign) {
                count++;
                int vertical = checkInsideFieldWinVertBottom(i + 1, j, sign, count);

                count = vertical;
                return count;
            } else {
//            count = 0; // НУЖЕН ЛИ??
            }

        } catch (RuntimeException e) {
            e.getMessage();
        }


        return count;

    }

    /**
     * @param i
     * @param j
     * @param sign
     * @param count
     * @return
     */
    public static int checkInsideFieldWinDiagonalBottom(int i, int j, char sign, int count) {

        try {
            if (field[i + 1][j + 1] == sign) {
                count++;
                int diagonal = checkInsideFieldWinDiagonalBottom(i + 1, j + 1, sign, count);

                count = diagonal;
                return count;
            } else {
//            count = 0; // НУЖЕН ЛИ??
            }

        } catch (RuntimeException e) {
            e.getMessage();
        }


        return count;

    }

    /**
     * @param i
     * @param j
     * @param sign
     * @param count
     * @return
     */
    public static int checkInsideFieldWinDiagonalTop(int i, int j, char sign, int count) {

        try {
            if (field[i - 1][j + 1] == sign) {
                count++;
                int diagonal = checkInsideFieldWinDiagonalTop(i - 1, j + 1, sign, count);

                count = diagonal;
                return count;
            } else {
            }

        } catch (RuntimeException e) {
            e.getMessage();
        }


        return count;

    }


    /**
     * Проверка на ничью
     *
     * @return TODO: переработать метод проверки
     */
    static boolean checkDraw() {
        for (int x = 0; x < fieldSizeX; x++) {
            for (int y = 0; y < fieldSizeY; y++)
                if (isCellEmpty(x, y)) return false;
        }
        return true;
    }

    /**
     * Метод проверки состояния игры
     *
     * @param playerSign - фишка игрока-пользователя
     * @param str
     * @return
     */
    static boolean gameCheck(char playerSign, String str) {
        if (checkWin(playerSign)) {
            System.out.println(str);
            return true;
        }
        if (checkDraw()) {
            System.out.println("Ничья!");
            return true;
        }

        return false; // Игра продолжается
    }

    static void analyzeAI(char playerSign, int countHorRight, int countVertBottom, int countDiagonalTop, int i, int j) {
        if (playerSign == DOT_HUMAN & countHorRight >= WIN_COUNT - ALARM_NUMBER) {  // |
            if(checkAddToArray(storageHorizonStepsAI, i,j )){

                if(j + (WIN_COUNT - ALARM_NUMBER) < fieldSizeX ){ // & field[i][j + (WIN_COUNT - ALARM_NUMBER )] == DOT_EMPTY
                    aiStep[0] = i;
                    aiStep[1] = j + (WIN_COUNT - ALARM_NUMBER);
                    System.out.println("Рекомендуемый ход для блокировки(по горизонтали):" + (aiStep[0]+1) + (aiStep[1]+1));

                }else if (j - (WIN_COUNT - ALARM_NUMBER) < fieldSizeX ) { // & field[i][j - (WIN_COUNT - ALARM_NUMBER - 1)] == DOT_EMPTY
                    aiStep[0] = i;
                    aiStep[1] = j - (WIN_COUNT - ALARM_NUMBER - 1);
                    System.out.println("Рекомендуемый ход для блокировки(по горизонтали):" + (aiStep[0]+1) + (aiStep[1]+1));
                }

                else {
                    aiStep[0] = fieldSizeX;
                }

            }



        } else if (playerSign == DOT_HUMAN & countVertBottom >= WIN_COUNT - ALARM_NUMBER) {  // |
            if (checkAddToArray(storageVerticalStepsAI, i, j)) {

                if (i + (WIN_COUNT - ALARM_NUMBER) < fieldSizeY) { // & field[i][j + (WIN_COUNT - ALARM_NUMBER )] == DOT_EMPTY
                    aiStep[0] = i + (WIN_COUNT - ALARM_NUMBER);
                    aiStep[1] = j;
                    System.out.println("Рекомендуемый ход для блокировки(по вертикали):" + (aiStep[0] + 1) + (aiStep[1] + 1));

                } else if (i - (WIN_COUNT - ALARM_NUMBER) < fieldSizeX) { // & field[i][j - (WIN_COUNT - ALARM_NUMBER - 1)] == DOT_EMPTY
                    aiStep[0] = i - (WIN_COUNT - ALARM_NUMBER + 1);
                    aiStep[1] = j;
                    System.out.println("Рекомендуемый ход для блокировки(по вертикали):" + (aiStep[0] + 1) + (aiStep[1] + 1));
                } else {
                    aiStep[0] = fieldSizeX;
                }


            }


        }
        else if (playerSign == DOT_HUMAN & countDiagonalTop >= WIN_COUNT - ALARM_NUMBER) {  // |
            if(checkAddToArray(storageDiagonalUpStepsAI, i,j )){

                if(i - (WIN_COUNT - ALARM_NUMBER) >= 0 & j + (WIN_COUNT - ALARM_NUMBER) < fieldSizeY ) { // & field[i][j + (WIN_COUNT - ALARM_NUMBER )] == DOT_EMPTY
                    aiStep[0] = i - (WIN_COUNT - ALARM_NUMBER);
                    aiStep[1] = j + (WIN_COUNT - ALARM_NUMBER);
                    System.out.println("Рекомендуемый ход для блокировки(по диагонали слева на право):" + (aiStep[0] + 1) + (aiStep[1] + 1));
                } else if (i + (WIN_COUNT - ALARM_NUMBER) >= 0 & (j - (WIN_COUNT - ALARM_NUMBER -1) < fieldSizeX) & j - (WIN_COUNT - ALARM_NUMBER -1) >0){
                    aiStep[0] = i + (WIN_COUNT - ALARM_NUMBER + 1);
                    aiStep[1] = j - (WIN_COUNT - ALARM_NUMBER - 1);
                    System.out.println("Рекомендуемый ход для блокировки(по диагонали слева на право):" + (aiStep[0] + 1) + (aiStep[1] + 1));
                }




            }
        }
        else if (playerSign == DOT_HUMAN & countVertBottom >= WIN_COUNT - ALARM_NUMBER) {  // |
            if(checkAddToArray(storageDiagonalDownStepsAI, i,j )){

                if(i + (WIN_COUNT - ALARM_NUMBER) <= fieldSizeY & j + (WIN_COUNT - ALARM_NUMBER) < fieldSizeX ) { // & field[i][j + (WIN_COUNT - ALARM_NUMBER )] == DOT_EMPTY
                    aiStep[0] = i + (WIN_COUNT - ALARM_NUMBER);
                    aiStep[1] = j + (WIN_COUNT - ALARM_NUMBER);
                    System.out.println("Рекомендуемый ход для блокировки(по диагонали слева на право вниз):" + (aiStep[0] + 1) + (aiStep[1] + 1));
                } else if (i - (WIN_COUNT - ALARM_NUMBER - 1) >= 0 & (j - (WIN_COUNT - ALARM_NUMBER -1) > 0) & j - (WIN_COUNT - ALARM_NUMBER -1) >0){
                    aiStep[0] = i - (WIN_COUNT - ALARM_NUMBER - 1);
                    aiStep[1] = j - (WIN_COUNT - ALARM_NUMBER - 1);
                    System.out.println("Рекомендуемый ход для блокировки(по диагонали слева на право вверх):" + (aiStep[0] + 1) + (aiStep[1] + 1));
                }
            }
        }


    }



    /**
     * Метод проверки на наличие элемента и добавления в массив
    */
    public static boolean checkAddToArray(String[] arr, Integer firstNum, Integer secondNum){
        String text = firstNum.toString()+ secondNum.toString();

        for(int i = 0; i < arr.length;i++){
            if(arr[i] == text ){
//                System.out.println("Метод checkAddToArray отработал корректно. False");
                return false;
            }
        }

        for (int i = 0; i< arr.length;i++){
            if(arr[i] == null){
                arr[i] = text;
            }
        }

//        System.out.println("Метод checkAddToArray отработал корректно. True");
        return true;
    }






}

