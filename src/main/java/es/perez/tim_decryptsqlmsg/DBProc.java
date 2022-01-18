package es.perez.tim_decryptsqlmsg;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class DBProc {

    private static Object IntelDec(Object obj, String clave) throws UnsupportedEncodingException {
        byte[] bArr;
        if (clave == null) {
            return obj;
        }
        if (obj instanceof String) {
            bArr = ((String)obj).getBytes("utf-8");
        } else if (!(obj instanceof byte[])) {
            return obj;
        } else {
            bArr = (byte[]) obj;
        }
        return Decrypt(bArr, clave);
    }


    public static Object Proc(String tipo, Object obj, String key) throws UnsupportedEncodingException {
        switch (tipo) {
            case "INTEGER":
                return obj instanceof Integer ? Integer.valueOf(((short) ((Integer) obj).intValue())) : obj;
            case "STRING":
            case "TEXT":
                return IntelDec(obj, key);
            default:
                return obj;
        }
    }


    public static byte[] toUTF8_Bytes(String str) {
        try {
            byte[] arrb = str.getBytes(StandardCharsets.UTF_8);
            return arrb;
        } catch (Exception uee) {
            uee.printStackTrace();
            return null;
        }
    }

    public static String toUTF8_String(byte[] bs) {
        try {
            String sa = new String(bs, StandardCharsets.UTF_8);
            return sa;
        } catch (Exception uee) {
            uee.printStackTrace();
            return "";
        }
    }

    private static HashMap getType(ResultSet rs, String col, String tipo) {
        HashMap<Object, Object> hashMap = new HashMap<>();
        try {
            switch (tipo) {
                case "STRING": // STRING
                    hashMap.put(col, rs.getString(col));
                    break;
                case "INTEGER": // INTEGER
                case "INT": // INT
                    hashMap.put(col, Integer.valueOf(rs.getInt(col)));
                    break;
                case "BLOB": // BLOB
                    hashMap.put(col, rs.getBytes(col));
                    break;
                case "BYTE": // BYTE
                    hashMap.put(col, Byte.valueOf(rs.getByte(col)));
                    break;
                case "CHAR": // CHAR
                    hashMap.put(col, rs.getString(col));
                    break;
                case "LONG": // LONG
                    hashMap.put(col, Long.valueOf(rs.getLong(col)));
                    break;
                case "REAL": // REAL
                    hashMap.put(col, Float.valueOf(rs.getFloat(col)));
                    break;
                case "TEXT": // TEXT
                    hashMap.put(col, rs.getString(col));
                    break;
                case "VARCHAR": // VARCHAR
                    hashMap.put(col, rs.getString(col));
                    break;
                default:
                    break;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hashMap;
    }

    private static ArrayList combine(ResultSet resultSet, Set set, HashMap hashMap) {
        ArrayList arrayList = new ArrayList();
        Iterator it = set.iterator();
        while (it.hasNext()) {
            String str = (String) it.next();
            arrayList.add(getType(resultSet, str, (String) hashMap.get(str)));
        }
        return arrayList;
    }


    public static ArrayList getValues(Connection cnn, HashMap hashMap) {
        String val = "select * from Friends;";
        Set set = hashMap.keySet();
        ArrayList<ArrayList> arrayList = new ArrayList();
        try {
            Statement statement = cnn.createStatement();
            ResultSet resultSet = statement.executeQuery(val);
            while (resultSet.next()) {
                arrayList.add(combine(resultSet, set, hashMap));
            }
            resultSet.close();
            statement.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
        return arrayList;
    }


    public static HashMap getColumns(Connection cnn) {
        String cmd = "SELECT * FROM Friends limit 0,1;";
        HashMap<String, String> hashMap = new HashMap<>(); // parameter 1 for value, parameter 2 for typename in the column
        try {
            Statement st = cnn.createStatement();
            ResultSet rst = st.executeQuery(cmd);
            if(rst.next()) {
                ResultSetMetaData rsmd = rst.getMetaData();
                int col = rsmd.getColumnCount();
                for(int b=0;b<col;b++) {
                    hashMap.put(rsmd.getColumnName(b+1), rsmd.getColumnTypeName(b+1));
                }
            }
            rst.close();
            st.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
        return hashMap;
    }

    public static String Decrypt(byte[] data, String key) {
        int length = key.length();
        byte[] b = toUTF8_Bytes(key);
        if (b == null) {
            return "";
        }
        int i = 0, i2 = 0;
        byte[] bArr2 = data.clone();
        while (i2 < data.length) {
            int length2 = 8 - Integer.toBinaryString((data[i2] ^ -1) & 255).length();
            if (length2 < 0 || length2 > 4) {
                System.err.println("Encoding error");
                break;
            }
            if (length2 == 0) {
                bArr2[i2] = (byte) (data[i2] ^ b[i % length]);
                i2++;
            } else {
                bArr2[(i2 + length2) - 1] = (byte) (data[(i2 + length2) - 1] ^ b[i % length]);
                i2 += length2;
            }
            i++;
        }
        return toUTF8_String(bArr2);
    }
}
