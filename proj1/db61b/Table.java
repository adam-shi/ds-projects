package db61b;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import static db61b.Utils.*;

/** A single table in a database.
 *  @author P. N. Hilfinger and Adam Shi
 */
class Table implements Iterable<Row> {
    /** A new Table whose columns are given by COLUMNTITLES, which may
     *  not contain duplicate names. */
    Table(String[] columnTitles) {
        for (int i = columnTitles.length - 1; i >= 1; i -= 1) {
            for (int j = i - 1; j >= 0; j -= 1) {
                if (columnTitles[i].equals(columnTitles[j])) {
                    throw error("duplicate column name: %s",
                                columnTitles[i]);
                }
            }
        }
        _titles = columnTitles;
    }

    /** A new Table whose columns are give by COLUMNTITLES. */
    Table(List<String> columnTitles) {
        this(columnTitles.toArray(new String[columnTitles.size()]));
    }

    /** Return the number of columns in this table. */
    public int columns() {
        return _titles.length;
    }

    /** Return the title of the Kth column.  Requires 0 <= K < columns(). */
    public String getTitle(int k) {
        return _titles[k];
    }

    /** Return the number of the column whose title is TITLE, or -1 if
     *  there isn't one. */
    public int findColumn(String title) {
        for (int i = 0; i < _titles.length; i++) {
            if (_titles[i].equals(title)) {
                return i;
            }
        }
        return -1;
    }

    /** Return the number of Rows in this table. */
    public int size() {
        return _rows.size();
    }

    /** Returns an iterator that returns my rows in an unspecified order. */
    @Override
    public Iterator<Row> iterator() {
        return _rows.iterator();
    }

    /** Add ROW to THIS if no equal row already exists.  Return true if anything
     *  was added, false otherwise. */
    public boolean add(Row row) {
        if (row.size() != _titles.length) {
            return false;
        }

        boolean add = true;
        int sizeBefore = _rows.size();
        for (Row oldRow: _rows) {
            if (row.equals(oldRow)) {
                add = false;
            }
        }
        if (add) {
            _rows.add(row);
        }
        int sizeAfter = _rows.size();
        return sizeBefore != sizeAfter;
    }

    /** Read the contents of the file NAME.db, and return as a Table.
     *  Format errors in the .db file cause a DBException. */
    static Table readTable(String name) {
        BufferedReader input;
        Table table;
        table = null;
        input = null;
        try {
            input = new BufferedReader(new FileReader(name + ".db"));
            String header = input.readLine();
            if (header == null) {
                throw error("missing header in DB file");
            }
            String[] columnNames = header.split(",");
            Table table2 = new Table(columnNames);
            String newRowData = input.readLine();
            while (newRowData != null) {
                String[] parsedNewRowData = newRowData.split(",");
                Row newRow = new Row(parsedNewRowData);
                table2.add(newRow);
                newRowData = input.readLine();
            }
            return table2;
        } catch (FileNotFoundException e) {
            throw error("could not find %s.db", name);
        } catch (IOException e) {
            throw error("problem reading from %s.db", name);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    /* Ignore IOException */
                }
            }
        }
    }

    /** Write the contents of TABLE into the file NAME.db. Any I/O errors
     *  cause a DBException. */
    void writeTable(String name) {
        PrintStream output;
        output = null;
        try {
            String sep;
            sep = ",";
            output = new PrintStream(name + ".db");
            output = output.append(_titles[0]);
            if (_titles.length > 1) {
                for (int i = 1; i < _titles.length; i++) {
                    output = output.append(sep + _titles[i]);
                }
            }
            output = output.append("\n");
            Iterator rowIterator = _rows.iterator();
            Row currentRow;
            for (int i = 0; i < _rows.size(); i++) {
                currentRow = (Row) rowIterator.next();
                output = output.append(currentRow.get(0));
                if (_titles.length > 1) {
                    for (int j = 1; j < _titles.length; j++) {
                        output = output.append(sep + currentRow.get(j));
                    }
                }
                output = output.append("\n");
            }
            output.close();

        } catch (IOException e) {
            throw error("trouble writing to %s.db", name);
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }

    /** Print my contents on the standard output. */
    void print() {
        Iterator rowIterator = iterator();
        Row toBePrinted;
        while (rowIterator.hasNext()) {
            toBePrinted = (Row) rowIterator.next();
            System.out.print("  ");
            for (int i = 0; i < toBePrinted.size(); i++) {
                System.out.print(toBePrinted.get(i) + " ");
            }
            System.out.println();
        }
    }

    /** Return a new Table whose columns are COLUMNNAMES, selected from
     *  rows of this table that satisfy CONDITIONS. */
    Table select(List<String> columnNames, List<Condition> conditions) {
        Table result = new Table(columnNames);

        Iterator columnIterator = columnNames.iterator();
        Iterator rowIterator = _rows.iterator();
        ArrayList<Column> columns = new ArrayList<Column>();

        while (columnIterator.hasNext()) {
            columns.add(new Column((String) columnIterator.next(), this));
        }

        Row evaluatedRow;
        Row selectedRow;

        for (int i = 0; i < _rows.size(); i++) {
            evaluatedRow = (Row) rowIterator.next();
            selectedRow = new Row(columns, evaluatedRow);
            if (conditions.size() > 0) {
                if (Condition.test(conditions, evaluatedRow)) {
                    result.add(selectedRow);
                }
            } else {
                result.add(selectedRow);
            }
        }

        return result;
    }

    /** Return a new Table whose columns are COLUMNNAMES, selected
     *  from pairs of rows from this table and from TABLE2 that match
     *  on all columns with identical names and satisfy CONDITIONS. */
    Table select(Table table2, List<String> columnNames,
                 List<Condition> conditions) {
        Table innerJoin = findInnerJoin(table2);
        Table result = innerJoin.select(columnNames, conditions);
        return result;
    }

    /** Return the inner join of THIS and TABLE2. */
    Table findInnerJoin(Table table2) {
        ArrayList<String> matchingTitles = new ArrayList<String>();
        Table results;
        for (int i = 0; i < _titles.length; i++) {
            for (int j = 0; j < table2._titles.length; j++) {
                if (_titles[i].equals(table2._titles[j])) {
                    matchingTitles.add(_titles[i]);
                }
            }
        }
        int matches = matchingTitles.size();
        Iterator matchTitlesIterator = matchingTitles.iterator();
        ArrayList<Column> common1 = new ArrayList<Column>();
        ArrayList<Column> common2 = new ArrayList<Column>();

        if (matchingTitles.size() == 0) {
            return cartesianProduct(table2);
        } else {
            String matchedTitle;
            ArrayList<Column> allColumns = new ArrayList<Column>();
            for (int i = 0; i < _titles.length; i++) {
                allColumns.add(new Column(_titles[i], this, table2));
            }
            for (int i = 0; i < table2._titles.length; i++) {
                if (!matchingTitles.contains(table2._titles[i])) {
                    allColumns.add(new Column(table2._titles[i], this, table2));
                }
            }
            int comboSize = allColumns.size();
            String[] columnTitles = new String[comboSize];
            Iterator columnIterator = allColumns.iterator();

            for (int i = 0; columnIterator.hasNext(); i++) {
                columnTitles[i] = ((Column) columnIterator.next()).getName();
            }
            Table result = new Table(columnTitles);

            for (int i = 0; i < matches; i++) {
                matchedTitle = (String) matchTitlesIterator.next();
                common1.add(new Column(matchedTitle, this));
                common2.add(new Column(matchedTitle, table2));
            }

            Row tempRow1;
            Row tempRow2;
            Iterator t1Iterator = _rows.iterator();
            Iterator t2Iterator;
            for (int i = 0; i < _rows.size(); i++) {
                t2Iterator = table2._rows.iterator();
                tempRow1 = (Row) t1Iterator.next();
                for (int j = 0; j < table2._rows.size(); j++) {
                    tempRow2 = (Row) t2Iterator.next();
                    if (Table.equijoin(common1, common2, tempRow1, tempRow2)) {
                        result.add(new Row(allColumns, tempRow1, tempRow2));
                    }
                }
            }
            return result;
        }
    }

    /** Return true if the columns COMMON1 from ROW1 and COMMON2 from
     *  ROW2 all have identical values.  Assumes that COMMON1 and
     *  COMMON2 have the same number of elements and the same names,
     *  that the columns in COMMON1 apply to this table, those in
     *  COMMON2 to another, and that ROW1 and ROW2 come, respectively,
     *  from those tables. */
    private static boolean equijoin(List<Column> common1, List<Column> common2,
                                    Row row1, Row row2) {

        Iterator c1Iterator = common1.iterator();
        Iterator c2Iterator = common2.iterator();
        String comp1;
        String comp2;

        for (int i = 0; i < common1.size(); i++) {
            comp1 = ((Column) c1Iterator.next()).getFrom(row1);
            comp2 = ((Column) c2Iterator.next()).getFrom(row2);
            if (!comp1.equals(comp2)) {
                return false;
            }
        }

        return true;
    }

    /** Returns the cartesian product of two tables THIS and TABLE2. */
    private Table cartesianProduct(Table table2) {
        String[] cpTitles = new String[_titles.length + table2._titles.length];
        for (int i = 0; i < cpTitles.length; i++) {
            if (i < _titles.length) {
                cpTitles[i] = _titles[i];
            } else {
                cpTitles[i] = table2._titles[i - _titles.length];
            }
        }
        Table cartProd = new Table(cpTitles);
        ArrayList<Column> columns = new ArrayList<Column>();
        for (int i = 0; i < cpTitles.length; i++) {
            columns.add(new Column(cpTitles[i], this, table2));
        }

        Row tempRow1;
        Row tempRow2;
        Iterator t1Iterator = _rows.iterator();
        Iterator t2Iterator;

        for (int i = 0; i < _rows.size(); i++) {
            tempRow1 = (Row) t1Iterator.next();
            t2Iterator = table2._rows.iterator();
            for (int j = 0; j < table2._rows.size(); j++) {
                tempRow2 = (Row) t2Iterator.next();
                cartProd.add(new Row(columns, tempRow1, tempRow2));
            }

        }
        return cartProd;
    }

    /** My rows. */
    private HashSet<Row> _rows = new HashSet<>();
    /** Titles of columns. */
    private String[] _titles;
}

