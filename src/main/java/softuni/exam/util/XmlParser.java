package softuni.exam.util;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;

public interface XmlParser {
    <T> T fromFile(String silePath, Class<T> tClass) throws JAXBException, FileNotFoundException;
    <T> void writeToFile(String filePath, T entity) throws JAXBException;
}

