package db61b;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Iterator;

/** Tests basic functionality of db61b, not including input token reading.
 *  @author Adam Shi
 */

public class Tests {
    /** Tests the Row class. */
    @Test
    public void testRow() {
        Row r = new Row(new String[] {"I", "like", "testing", "things"});
        assertEquals(4, r.size());
        assertEquals("like", r.get(1));
        Row r2 = new Row(new String[] {"I", "like", "testing", "things"});
        Row r3 = new Row(new String[] {"I", "like", "testing"});
        assertTrue(r.equals(r2));
        assertTrue(!r.equals(r3));
    }

    /** Tests the complex row constructor. */
    @Test
    public void testRowConstructor() {
        ArrayList<Column> columns = new ArrayList<Column>();

        Table t1 = Table.readTable("students");
        Table t2 = Table.readTable("enrolled");
        Iterator t1Iterator = t1.iterator();
        Iterator t2Iterator = t2.iterator();
        Row[] rows = new Row[2];
        rows[0] = (Row) t1Iterator.next();
        rows[1] = (Row) t2Iterator.next();

        columns.add(new Column("Firstname", t1, t2));
        columns.add(new Column("Grade", t1, t2));
        Row r = new Row(columns, rows);
        assertEquals(rows[0].get(2), r.get(0));
        assertEquals(rows[1].get(2), r.get(1));
        assertEquals(r, new Row(new String[] {rows[0].get(2), rows[1].get(2)}));

    }

    /** Tests the Table class. */
    @Test
    public void testTable() {
        Row r = new Row(new String[] {"1", "2", "3", "4"});
        Row r2 = new Row(new String[] {"1", "2", "3", "4"});
        Table t = new Table(new String[] {"1", "2", "3", "4"});
        ArrayList<String> columnTitles = new ArrayList<String>();
        columnTitles.add("1");
        columnTitles.add("2");
        columnTitles.add("3");
        columnTitles.add("4");

        Table t2 = new Table(columnTitles);

        for (int i = 0; i < 4; i++) {
            assertEquals(t.getTitle(i), t2.getTitle(i));
        }
        try {
            Table badT = new Table(new String[] {"1", "1", "2"});
            fail("Should have thrown exception");
        } catch (DBException d) {
            assertEquals("duplicate column name: 1", d.getMessage());
        }
        assertEquals(4, t.columns());
        assertEquals("4", t.getTitle(3));
        assertEquals(2, t.findColumn("3"));
        assertTrue(t.add(r));
        assertTrue(!t.add(r));
        assertTrue(!t.add(r2));
        assertEquals(1, t.size());
    }

    /** Tests the Database class. */
    @Test
    public void testDatabase() {
        Database d = new Database();
        Table t = new Table(new String[] {"1", "2", "3", "4"});
        d.put("table1", t);
        assertEquals(t, d.get("table1"));
        Table t2 = new Table(new String[] {"1b", "2b", "3b", "4b"});
        d.put("table1", t2);
        assertEquals(t2, d.get("table1"));
    }

    /** Tests more complex table functions: read, write, load, select */
    @Test
    public void testTableComplex() {
        Table blank = Table.readTable("blank");
        assertEquals("Second", blank.getTitle(1));
        blank.add(new Row(new String[] {"1", "2", "3"}));
        blank.writeTable("blank2");
        Table blank2 = Table.readTable("blank2");
        assertEquals("Third", blank2.getTitle(2));
        Iterator b2Iterator = blank2.iterator();
        assertEquals(new Row(new String[] {"1", "2", "3"}),
                     (Row) b2Iterator.next());

        ArrayList<String> selectColumns = new ArrayList<String>();
        selectColumns.add("Second");
        selectColumns.add("Third");
        ArrayList<Condition> conditions = new ArrayList<Condition>();
        Table select1 = blank2.select(selectColumns, conditions);
        Iterator selectIterator = select1.iterator();
        assertEquals(new Row(new String[] {"2", "3"}),
                     (Row) selectIterator.next());

        blank2.add(new Row(new String[] {"4", "5", "6"}));
        Column column1 = new Column("Third", blank2);
        conditions.add(new Condition(column1, "=", "6"));
        Table select2 = blank2.select(selectColumns, conditions);
        Iterator select2Iterator = select2.iterator();
        assertEquals(new Row(new String[] {"5", "6"}),
                     (Row) select2Iterator.next());

        blank2.add(new Row(new String[] {"7", "8", "6"}));
        Column column2 = new Column("Second", blank2);
        conditions.add(new Condition(column2, "=", "5"));
        Table select3 = blank2.select(selectColumns, conditions);
        Iterator select3Iterator = select3.iterator();
        assertEquals(new Row(new String[] {"5", "6"}),
                     (Row) select3Iterator.next());
    }

    /** Tests the two table select. */
    @Test
    public void testTwoSelect() {
        Table students = Table.readTable("students");
        Table enrolled = Table.readTable("enrolled");
        Table expected = Table.readTable("selectResult");
        ArrayList<String> columnNames = new ArrayList<String>();
        columnNames.add("SID");
        columnNames.add("Firstname");
        columnNames.add("Lastname");
        columnNames.add("CCN");
        ArrayList<Condition> conditions = new ArrayList<Condition>();
        Column col1 = new Column("SID", students);
        String relation = "=";
        String val2 = "101";
        conditions.add(new Condition(col1, relation, val2));
        Table actual = students.select(enrolled, columnNames, conditions);

        Iterator actualIterator = actual.iterator();
        Iterator expectedIterator = expected.iterator();

        while (actualIterator.hasNext()) {
            assertEquals((Row) actualIterator.next(),
                         (Row) expectedIterator.next());
        }
    }

    public static void main(String[] args) {
        ucb.junit.textui.runClasses(Tests.class);
    }
}
