package myPackage;

import java.io.IOException;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

/**
 * https://github.com/cassiomolin/lucene-example
 * 
 * @author claud
 *
 */
public class MyClass {

  public static void main(String[] args) {
    Directory index = new RAMDirectory();
    
    StandardAnalyzer  analyzer    = new StandardAnalyzer();
    IndexWriterConfig config      = new IndexWriterConfig(analyzer);

    Document document1 = new Document();
    document1.add(new TextField("name", "Jane Doe", Field.Store.YES));
    document1.add(new TextField("address", "9 Main Circle", Field.Store.YES));
    Document document2 = new Document();
    document2.add(new TextField("name", "John Smith", Field.Store.YES));
    document2.add(new TextField("address", "9 Dexter Avenue", Field.Store.YES));
    
    try {
      IndexWriter writer = new IndexWriter(index, config);
      writer.addDocument(document1);
      writer.addDocument(document2);
      writer.close();

      IndexReader   reader   = DirectoryReader.open(index);
      IndexSearcher searcher = new IndexSearcher(reader);
      
      Query query = new TermQuery(new Term("name", "doe"));
      
      TopDocs topDocs = searcher.search(query, 10);
      
      for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
        Document document = searcher.doc(scoreDoc.doc);
        System.out.println(document.get("address"));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    

  }

}
