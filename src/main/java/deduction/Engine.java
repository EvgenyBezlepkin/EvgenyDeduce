package deduction;

import deduction.db.ParserDB;
import deduction.txt.ParserTxt;
import deduction.model.Model;
import deduction.db.WriterDB;
import deduction.txt.WriterTxt;
import deduction.xml.ParserXml;
import deduction.xml.WriterXml;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;


public class Engine {

    public enum FormatEnum {
        TXT, XML, DB
    }


    private SqlSessionFactory ssf;


     public void createSqlSessionFactory(String filename) {
         try {
             ssf = new SqlSessionFactoryBuilder().build(new FileReader(filename));
         } catch (FileNotFoundException e) {
             System.out.println(e.getMessage());
         }
     }

    public void deduce(String file, FormatEnum fmt) {
        Model model;
        Parser parser;
        Collection<String> resultsList;
        try {
            parser = createParser(fmt);
            model = parser.parse(file);
            resultsList = model.deduce();
        } catch (FileNotFoundException e) {
            System.err.print("Wrong argument: file not found");
            return;
        } catch (IOException e) {
            System.err.print("Error when reading file: " + e.getMessage());
            return;
        } catch (ParserException e) {
            System.err.print(e.getMessage());
            return;
        } catch (Exception e) {
            System.err.print("Error: " + e.getMessage());
            return;
        }
        StringBuilder sb = new StringBuilder();
        Iterator<String> i = resultsList.iterator();
        if (i.hasNext())
            sb.append(i.next());
        while (i.hasNext()) {
            sb.append(", ").append(i.next());
        }
        System.out.print(sb);
    }


    private Parser createParser(FormatEnum fmt) throws Exception {
        switch (fmt) {
            case TXT:
                return new ParserTxt();
            case XML:
                return new ParserXml();
            case DB:
                return new ParserDB(ssf);
            default:
                throw new Exception("Unknown parser format");
        }
    }


    public void convert(String inputFile, FormatEnum fmtin, String outputFile, FormatEnum fmtout) {
        Model model;
        Parser parser;

        if (fmtin == fmtout) {
            System.err.println("Error: format input file and format output file are the same");
        } else
            try {
                parser = createParser(fmtin);
                model = parser.parse(inputFile);

                Writer writer = createWriter(fmtout);
                writer.convert(outputFile, model);
                System.out.print("Conversion is done");
            } catch (IOException e) {
                System.err.print("Invalid argument: " + e.getMessage());
            } catch (SAXException e) {
                System.err.print("Invalid file syntax: " + e.getMessage());
            } catch (JAXBException e) {
                System.err.print("Invalid file syntax: " + e.getMessage());
            } catch (Exception e) {
                System.err.print(e.getCause().getMessage().substring(0, e.getCause().getMessage().indexOf("\n")));
            }
    }


    private Writer createWriter(FormatEnum fmt) throws Exception {
        switch (fmt) {
            case TXT:
                return new WriterTxt();
            case XML:
                return new WriterXml();
            case DB:
                return new WriterDB(ssf);
            default:
                throw new Exception("Unknown parser format");
        }
    }

    public void deleteDB(String inputFile) {
        try {
            WriterDB writer = new WriterDB(ssf);
            writer.deleteModelDB(inputFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}

