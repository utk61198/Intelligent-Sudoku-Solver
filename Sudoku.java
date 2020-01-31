package Sudoku;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.Buffer;
import javax.swing.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
import javax.imageio.ImageIO;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import jdk.jfr.events.FileReadEvent;

public class Sudoku extends JFrame {

    JFileChooser file_chooser = new JFileChooser();
    JTable sudoku_board = new JTable(9, 9);
    JButton solve_button = new JButton("Solve Sudoku!");
    JButton reset_sudoku = new JButton("Reset Board!");
    JLabel heading = new JLabel("Sudoku Solver!");
    JButton input_from_file = new JButton("Input from File!");
    JTable table_temp = new JTable(9, 9);
    JFrame tempFrame = new JFrame();
    JButton[] tempButton = new JButton[81];

    public class sudokuBoardColor extends DefaultTableCellRenderer {

        public Component getTableCellRendererComponent(JTable table,
                                                       Object value, boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            Border border;

            if ((column == 3 || column == 4 || column == 5) && (row == 3 || row == 4 || row == 5)) {
                border = BorderFactory.createLineBorder(Color.RED, 5);

            } else if ((column == 0 || column == 1 || column == 2) && (row == 0 || row == 1 || row == 2)) {
                border = BorderFactory.createLineBorder(Color.RED, 5);

            } else if ((column == 6 || column == 7 || column == 8) && (row == 0 || row == 1 || row == 2)) {
                border = BorderFactory.createLineBorder(Color.RED, 5);

            } else if ((column == 0 || column == 1 || column == 2) && (row == 6 || row == 7 || row == 8)) {
                border = BorderFactory.createLineBorder(Color.RED, 5);

            } else if ((column == 6 || column == 7 || column == 8) && (row == 6 || row == 7 || row == 8)) {
                border = BorderFactory.createLineBorder(Color.RED, 5);

            }
            else {
                border = BorderFactory.createLineBorder(new Color(153, 255, 153), 5);

            }

            JComponent comp = (JComponent) super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
                    row, column);

            comp.setBorder(border);

            return comp;

        }
    }

    public Sudoku() {
        tempFrame.setLayout(new GridLayout(9, 9));

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                int[] temp = new int[2];
                temp[0] = i;
                temp[1] = j;

            }
        }

        sudoku_board.setColumnSelectionAllowed(false);
        sudoku_board.setRowSelectionAllowed(false);
        Font heading_font = new Font("Comic Sans MS", Font.BOLD, 100);
        heading.setFont(heading_font);

        Font board_font = new Font("Serif bold", Font.BOLD, 55);
        Font btn_font = new Font("Serif bold", Font.BOLD, 20);
        sudoku_board.setFont(board_font);
        solve_button.setFont(btn_font);
        reset_sudoku.setFont(btn_font);
        input_from_file.setFont(btn_font);
        solve_button.setForeground(Color.YELLOW);
        solve_button.setBackground(Color.BLACK);
        reset_sudoku.setForeground(Color.YELLOW);
        reset_sudoku.setBackground(Color.BLACK);
        input_from_file.setBackground(Color.black);
        input_from_file.setForeground(Color.YELLOW);

        JTextField textField = new JTextField();
        textField.setFont(new Font("Serif bold", Font.BOLD, 80));
        textField.setBorder(new LineBorder(Color.BLACK));
        DefaultCellEditor dce = new DefaultCellEditor(textField);
        for (int i = 0; i < 9; i++) {
            sudoku_board.getColumnModel().getColumn(i).setCellEditor(dce);

        }

        solve_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {

                boolean flag = true;
                for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 9; j++) {

                        if (sudoku_board.getValueAt(i, j) != null) {
                            int val = Integer.parseInt(sudoku_board.getValueAt(i, j).toString());
                            if (val > 9 || val < 1) {
                                flag = false;

                            }
                        }
                    }
                }
                if (checkWrongInput()) {
                    flag = false;
                }

                if (flag == false) {
                    Component frame = null;
                    JOptionPane.showMessageDialog(frame, "OOPS!! LOOKS LIKE A WRONG INPUT BY THE USER");
                    for (int i = 0; i < 9; i++) {
                        for (int j = 0; j < 9; j++) {
                            sudoku_board.setValueAt(null, i, j);
                        }
                    }

                }

                if (flag == true) {
                    Timer timer = new Timer(1000, null);
                    SwingWorker swing_worker = new SwingWorker() {
                        @Override
                        protected String doInBackground() throws Exception {
                            solveGame();
                            sudoku_board.setEnabled(false);
                            return "done";
                        }
                    };
                    swing_worker.execute();

                }

            }
        });

        reset_sudoku.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                sudoku_board.setEnabled(true);
                for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 9; j++) {
                        sudoku_board.setValueAt(null, i, j);
                    }
                }

            }
        });
        input_from_file.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                int[][] board_arr = new int[9][9];

                int var = file_chooser.showOpenDialog(null);
                if (var == JFileChooser.APPROVE_OPTION) {

                    File file = new File(file_chooser.getSelectedFile().getAbsolutePath());
                    try {
                        FileReader file_reader = new FileReader(file);
                        BufferedReader buffered_reader = new BufferedReader(file_reader);
                        String csv_line = "";
                        int row_number = 0;
                        while ((csv_line = buffered_reader.readLine()) != null) {
                            String[] holding_array = csv_line.split(",");
                            for (int i = 0; i < 9; i++) {
                                board_arr[row_number][i] = Integer.parseInt(holding_array[i]);
                            }

                            row_number++;

                        }

                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(Sudoku.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(Sudoku.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    for (int i = 0; i < 9; i++) {
                        for (int j = 0; j < 9; j++) {
                            if (board_arr[i][j] == 0) {
                                sudoku_board.setValueAt(null, i, j);

                            } else {
                                sudoku_board.setValueAt(board_arr[i][j], i, j);
                            }
                        }
                    }
                }

            }
        });

        this.setLayout(null);
        this.setSize(1920, 1080);
        sudoku_board.setRowHeight(100);
        sudoku_board.setBounds(0, 0, 900, 900);
        solve_button.setBounds(1300, 300, 200, 70);
        reset_sudoku.setBounds(1300, 400, 200, 70);
        input_from_file.setBounds(1300, 500, 200, 70);

        heading.setBackground(Color.BLACK);
        JPanel temp_panel = new JPanel();
        temp_panel.add(heading);
        temp_panel.setBounds(950, 10, 900, 190);
        temp_panel.setBackground(Color.BLACK);
        heading.setForeground(Color.YELLOW);
        sudokuBoardColor sudoku_color = new sudokuBoardColor();
        sudoku_color.setHorizontalAlignment(JLabel.CENTER);
        sudoku_board.setDefaultRenderer(Object.class, sudoku_color);
        sudoku_board.setForeground(Color.WHITE);
        sudoku_board.setBackground(Color.BLACK);
        this.add(sudoku_board);
        this.add(input_from_file);

        this.add(solve_button);
        this.add(reset_sudoku);
        this.add(temp_panel);

    }

    public boolean solveGame() throws InterruptedException {

        if (checkFullSudoku()) {
            return true;
        }
        String[] empty_pos_index = (firstEmptyPos()).split(",");
        int row_index = Integer.parseInt(empty_pos_index[0]);
        int column_index = Integer.parseInt(empty_pos_index[1]);

        for (int i = 1; i <= 9; i++) {

            if (safeBox(row_index, column_index, i) && safeRow(row_index, column_index, i) && safeColumn(row_index, column_index, i)) {

                sudoku_board.setValueAt(i, row_index, column_index);
                Thread.sleep(100);

                if (solveGame()) {
                    return true;
                } else {
                    sudoku_board.setValueAt(null, row_index, column_index);
                }

            }

        }

        return false;

    }

    public boolean checkWrongInput() {
        boolean flag = false;
        for (int i = 0; i < 9; i++) {
            HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
            for (int j = 0; j < 9; j++) {
                if (sudoku_board.getValueAt(i, j) != null) {
                    if (map.containsKey(Integer.parseInt(sudoku_board.getValueAt(i, j).toString()))) {
                        flag = true;
                    } else {
                        map.put(Integer.parseInt(sudoku_board.getValueAt(i, j).toString()), 1);

                    }
                }

            }
        }
        for (int i = 0; i < 9; i++) {
            HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
            for (int j = 0; j < 9; j++) {
                if (sudoku_board.getValueAt(j, i) != null) {
                    if (map.containsKey(Integer.parseInt(sudoku_board.getValueAt(j, i).toString()))) {
                        flag = true;
                    } else {
                        map.put(Integer.parseInt(sudoku_board.getValueAt(j, i).toString()), 1);

                    }
                }

            }
        }

        for (int i = 0; i < 9; i = i + 3) {
            for (int j = 0; j < 9; j = j + 3) {
                int row = i - i % 3;
                int col = j - j % 3;
                HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();

                for (int n = 0; n < 3; n++) {

                    for (int m = 0; m < 3; m++) {
                        if (sudoku_board.getValueAt(n + row, m + col) != null) {
                            if (map.containsKey(Integer.parseInt(sudoku_board.getValueAt(n + row, m + col).toString()))) {
                                flag = true;
                            } else {
                                map.put(Integer.parseInt(sudoku_board.getValueAt(n + row, m + col).toString()), 1);

                            }

                        }

                    }
                }
            }
        }

        return flag;

    }

    public String firstEmptyPos() {

        int[] empty_index = new int[2];

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {

                if (sudoku_board.getValueAt(i, j) == null) {
                    return i + "," + j;

                }

            }
        }

        return null;
    }

    public boolean checkFullSudoku() {
        boolean flag = true;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (sudoku_board.getValueAt(i, j) == null) {
                    flag = false;
                }
            }

        }
        return flag;
    }

    public boolean safeColumn(int row, int col, int val) {
        boolean flag = true;
        for (int i = 0; i < 9; i++) {
            if (sudoku_board.getValueAt(i, col) != null) {
                int temp_val = Integer.parseInt(sudoku_board.getValueAt(i, col).toString());
                if (temp_val == val) {
                    flag = false;
                }
            }

        }

        return flag;
    }

    public boolean safeRow(int row, int col, int val) {
        boolean flag = true;

        for (int i = 0; i < 9; i++) {
            if (sudoku_board.getValueAt(row, i) != null) {
                int temp_val = Integer.parseInt(sudoku_board.getValueAt(row, i).toString());
                if (temp_val == val) {
                    flag = false;

                }
            }
        }

        return flag;

    }

    public boolean safeBox(int row, int col, int val) {
        boolean flag = true;

        int r = row - (row % 3);
        int c = col - col % 3;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (sudoku_board.getValueAt(i + r, j + c) != null) {
                    int temp_val = Integer.parseInt(sudoku_board.getValueAt(i + r, j + c).toString());
                    if (temp_val == val) {
                        flag = false;
                    }
                }

            }
        }

        return flag;

    }

    public static void main(String[] args) {
        Sudoku obj = new Sudoku();

        obj.setVisible(true);
        obj.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

}
