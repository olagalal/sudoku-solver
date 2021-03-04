package sudokusolver;

import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class SudokuSolver implements ActionListener {

    static JFrame f;

    JPanel cont, p, p1, p2;
    static JButton btnSolve, btnReset;
    static int x, y;
    static final JTextField[] tf = new JTextField[81];
    static JRadioButton rb1, rb2;

    static int[][] sudoku = new int[9][9];
    static double time = 0.00;
    static boolean flag = false;

    public SudokuSolver() {
        f = new JFrame("Sudoku Solver");

        cont = new JPanel();
        cont.setLayout(new BorderLayout());

        p = new JPanel(new GridLayout(9, 9));           //has fixed 9

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                final int index = i * 9 + j ;
                tf[i * 9 + j] = new JTextField("", 1);
                tf[i * 9 + j].setHorizontalAlignment(JTextField.CENTER);
                tf[i * 9 + j].setFont(new Font("Arial", Font.BOLD, 20));
                tf[i * 9 + j].addKeyListener(new KeyListener() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        
                    }

                    @Override
                    public void keyPressed(KeyEvent e) {
                    
                    }

                    @Override
                    public void keyReleased(KeyEvent e) {
//                        if(tf[index].getText().length() > 1){
//                            
//                            tf[index].setText("" + tf[index].getText().charAt(0));
//                            
//                        }else 
                            if(!Character.isDigit(e.getKeyChar())){

                            tf[index].setText("");
                            
                        }
                    }
                });
                p.add(tf[i * 9 + j]);
            }
        }

        //color boxes differently
        coloringGrid();
        
        p1 = new JPanel();

        btnSolve = new JButton("Solve Sudoku");
        btnSolve.addActionListener(this);
        p1.add(btnSolve);

        btnReset = new JButton("Reset Sudoku");
        btnReset.addActionListener(this);
        p1.add(btnReset);

        cont.add(p, BorderLayout.CENTER);
        cont.add(p1, BorderLayout.SOUTH);

        f.add(cont);
        f.pack();
        f.setVisible(true);
        f.setSize(500, 510);
        f.setLocationRelativeTo(null);
        f.setResizable(false);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void actionPerformed(ActionEvent e) {
        
        // Action of Click Solve Button
        if (e.getSource() == btnSolve) {

            solve();

            if (flag) {
                btnSolve.setEnabled(false);
                for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 9; j++) {

                        tf[i * 9 + j].setEditable(false);
                    }
                }
                btnSolve.setText("Solved! Time : " + time + " Seconds.");
            } else {
                btnSolve.setText("Solve Sudoku");
            }
        }

        // Action of Click Reset Button
        if (e.getSource() == btnReset) {
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    tf[i * 9 + j].setEditable(true);
                }
            }

            btnSolve.setText("Solve Sudoku");
            btnSolve.setEnabled(true);
            
            reset();
        }
    }


    public static void coloringGrid() {
        for (int i = 0; i < (int) Math.sqrt(9); i++) {
            for (int j = 0; j < (int) Math.sqrt(9); j++) {
                if ((i + j) % 2 != 0) {
                    int r = i * ((int) Math.sqrt(9));
                    int c = j * ((int) Math.sqrt(9));
                    for (int x = 0; x < (int) Math.sqrt(9); x++) {
                        for (int y = 0; y < (int) Math.sqrt(9); y++) {
                            tf[(r + x) * 9 + (c + y)].setBackground(Color.LIGHT_GRAY);
                        }
                    }

                }
            }
        }
    }

    private static void reset() {
        for (int i = 0; i < 81; i++) {
            try {
                tf[i].setText("");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(f, e);
            }
        }
        JOptionPane.showMessageDialog(f, "Sudoku Successfully Reset.");
    }

    private static void solve() {
        long t1 = System.currentTimeMillis();

        makeSudoku();

        if (validate()) {
            if (solveSudoku()) {
                flag = true;
            } else {
                JOptionPane.showMessageDialog(f, "Invalid sudoku ! Please try Again. Time : " + time + " Sec.");
            }
        } else {
            JOptionPane.showMessageDialog(f, "Invalid sudoku ! Please try Again\nMaybe some value not between 1 and " + 9);
        }

        time = (System.currentTimeMillis() - t1) / 1000.000;
    }

    private static void makeSudoku() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (((tf[i * 9 + j]).getText()).equals("")) {
                    sudoku[i][j] = 0;
                } else {
                    sudoku[i][j] = Integer.parseInt((tf[i * 9 + j]).getText());
                }
            }
        }
    }

    private static boolean solveSudoku() {
        int row = 0, col = 0;
        boolean flag1 = false;
        
        //find unassigned location
        for (row = 0; row < 9; row++) {
            for (col = 0; col < 9; col++) {
                if (sudoku[row][col] == 0) {
                    flag1 = true;
                    break;
                }
            }
            if (flag1 == true) {
                break;
            }
        }

        if (flag1 == false) //if no unassigned
        {
            return true;
        }

        //SOLVE > Assign numbers to 0s 
        for (int n = 1; n <= 9; n++) {
            if (isSafe(row, col, n)) {
                
                //assignment
                sudoku[row][col] = n;
                
                //print output;
                (tf[(row) * 9 + col]).setText(Integer.toString(n));
                
                //return if Success
                if (solveSudoku()) {
                    return true;
                }
                
                //if Fail, undo and try again
                sudoku[row][col] = 0;
            }
        }

        //trigger backtracking
        return false;
    }

    private static boolean validate() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (sudoku[i][j] < 0 || sudoku[i][j] > 9) {
                    return false;
                }

                if (sudoku[i][j] != 0 && (usedInRow(i, j, sudoku[i][j]) || usedInCol(i, j, sudoku[i][j]) || usedInBox(i, j, sudoku[i][j]))) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean isSafe(int r, int c, int n) {
        return (!usedInRow(r, c, n) && !usedInCol(r, c, n) && !usedInBox(r, c, n));
    }

    private static boolean usedInRow(int r, int c, int n) {
        for (int col = 0; col < 9; col++) {
            if (col != c && sudoku[r][col] == n) {
                return true;
            }
        }
        return false;
    }

    private static boolean usedInCol(int r, int c, int n) {
        for (int row = 0; row < 9; row++) {
            if (row != r && sudoku[row][c] == n) {
                return true;
            }
        }
        return false;
    }

    private static boolean usedInBox(int r, int c, int n) {
        int r_st = r - r % ((int) Math.sqrt(9));
        int c_st = c - c % ((int) Math.sqrt(9));

        for (int i = 0; i < (int) Math.sqrt(9); i++) {
            for (int j = 0; j < (int) Math.sqrt(9); j++) {
                if (r_st + i != r && c_st + j != c && sudoku[r_st + i][c_st + j] == n) {
                    return true;
                }
            }
        }
        return false;
    }
   
    public static void main(String[] args) throws IOException {
        new SudokuSolver();
    }
    
}