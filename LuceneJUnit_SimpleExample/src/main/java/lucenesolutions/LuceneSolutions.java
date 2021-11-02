package lucenesolutions;

import java.io.IOException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

/**
 * https://gist.github.com/edyluisrey/3519529
 * 
 * @author claud
 *
 */
public class LuceneSolutions {

  public static void main(String[] args) {
    Analyzer          analyzer = new SpanishAnalyzer();
//    Analyzer          analyzer = new StandardAnalyzer();
    Directory         index    = new RAMDirectory();
    IndexWriterConfig config   = new IndexWriterConfig(analyzer);
    
    try {
      IndexWriter w = new IndexWriter(index, config);
      
      addDoc(w, "programador backend");
      addDoc(w, "programador frontend");
      addDoc(w, "Necestamos programadores");
      addDoc(w, "ciencia de computacion");
      w.close();
      
      String querystr = args.length > 0 ? args[0] : "programador";
      
      Query q = new QueryParser("title", analyzer).parse(querystr);
      
      int                  hitsPerPage = 10;
      IndexReader          r           = DirectoryReader.open(index);
      IndexSearcher        s           = new IndexSearcher(r);
      TopScoreDocCollector collector   = TopScoreDocCollector.create(hitsPerPage);
      s.search(q, collector);
      ScoreDoc[] hits = collector.topDocs().scoreDocs;
      
      System.out.println("Found " + hits.length + " hits.");
      for (ScoreDoc hit : hits) {
        int      docId = hit.doc;
        Document d     = s.doc(docId);
        System.out.println(d.get("title"));
      }
    } catch (IOException | ParseException e) {
      e.printStackTrace();
    }
    
  }
  
  private static void addDoc(IndexWriter w, String value) throws IOException {
    Document document = new Document();
    document.add(new TextField("title", value, Field.Store.YES));
    w.addDocument(document);
  }

}
