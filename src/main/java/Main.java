import au.com.bytecode.opencsv.CSVWriter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        String[] employee = ("1,John,Smith,USA,25").split(",");
        String[] employee1 = ("2,Ivan,Petrov,RU,23").split(",");
// Создаем экземпляр CSVWriter
        try (CSVWriter writer = new CSVWriter(new FileWriter("data.csv"))) {
// Записываем запись в файл
            writer.writeNext(employee);
            writer.writeNext(employee1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        String fileNameJson = "new_data.json";
        String fileNameJson2 = "data2.json";

        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        writeString(fileNameJson, json);

        List<Employee> list2 = parseXML("data.xml");
        String json2 = listToJson(list2);
        writeString(fileNameJson2, json2);

        String json3 = readString("new_data.json");
        List<Employee> list3 = jsonToList(json3);
        list3.forEach(System.out::println);
    }

    public static List<Employee> parseCSV(String[] columnMapping, String fileCsvName) {
        List<Employee> list = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(new FileReader(fileCsvName))) {
            ColumnPositionMappingStrategy<Employee> strategy =
                    new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            list = csv.parse();
//            list.forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<Employee> parseXML(String file) {
        List<Employee> list2 = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(file));
            Node root = doc.getDocumentElement();
//            System.out.println("Корневой элемент: " + root.getNodeName());
            NodeList nodeList = root.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
//                for (int i = 0; i < list2.getLength(); i++) {
                Node node = nodeList.item(i);
//                System.out.println("Teкyщий элeмeнт: " + node.getNodeName());

                if (Node.ELEMENT_NODE == node.getNodeType()) {
                    Element employee = (Element) node;
//                    System.out.println("id: " +
//                            employee.getElementsByTagName("id").item(0).getTextContent());
//                    System.out.println("firstName: " +
//                            employee.getElementsByTagName("firstName").item(0).getTextContent());
//                    System.out.println("lastName: " +
//                            employee.getElementsByTagName("lastName").item(0).getTextContent());
//                    System.out.println("country: " +
//                            employee.getElementsByTagName("country").item(0).getTextContent());
//                    System.out.println("age: " +
//                            employee.getElementsByTagName("age").item(0).getTextContent());

                    Employee employee2 = new Employee(Integer.parseInt(employee.getElementsByTagName("id").item(0).getTextContent()),
                            employee.getElementsByTagName("firstName").item(0).getTextContent(),
                            employee.getElementsByTagName("lastName").item(0).getTextContent(),
                            employee.getElementsByTagName("country").item(0).getTextContent(),
                            Integer.parseInt(employee.getElementsByTagName("age").item(0).getTextContent()));
                    list2.add(employee2);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list2;
    }

    public static String listToJson(List<Employee> list) { // преобразовываем список сотрудников в строку Json
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
//        System.out.println(gson.toJson(list));
        String json = gson.toJson(list, listType);
        return json;
    }

    public static void writeString(String fileNameJson, String json) { // записываем строку в файл
        try (FileWriter file = new
                FileWriter(fileNameJson)) {
            file.write(json.toString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readString(String fileName) {
        JSONParser parser = new JSONParser();
        String json = null;
        try (BufferedReader bf = new BufferedReader(new FileReader(fileName))) {
            Object obj = parser.parse(bf);
            JSONArray jsonArray = (JSONArray) obj;
            json = jsonArray.toJSONString();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static List<Employee> jsonToList(String json) {
        List<Employee> list = new ArrayList<>();
        GsonBuilder builder = new GsonBuilder();
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(json);
            JSONArray jsonArray = (JSONArray) obj;
            Gson gson = builder.create();
            for (Object jsonObject : jsonArray) {
                Employee employee = gson.fromJson(jsonObject.toString(), Employee.class);
                list.add(employee);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return list;
    }
    
}
