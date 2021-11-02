package myPackage;

import java.io.IOException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Attribute;

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
    Document document3 = new Document();
    document3.add(new TextField("name", "Claude Lepère", Field.Store.YES));
    String address3 = "St. Martin in the Fields Church Path";
    document3.add(new TextField("address", address3, Field.Store.YES));
    
    try {
      IndexWriter writer = new IndexWriter(index, config);
      writer.addDocument(document1);
      writer.addDocument(document2);
      writer.addDocument(document3);
      writer.close();
      
//      System.out.println(getTokenStream(analyzer, "address", "9 Main Circle"));
      System.out.println(getTokenStream(analyzer, "address", address3));

      IndexReader   reader   = DirectoryReader.open(index);
      IndexSearcher searcher = new IndexSearcher(reader);
      
//      Query       query      = new TermQuery(new Term("name", "doe"));
//      Query       query      = new TermQuery(new Term("address", "Main Circle"));  // 0 hit
      PhraseQuery phraseQuery = new PhraseQuery();
      phraseQuery.add(new Term("address", "dexter"), 0);
      phraseQuery.add(new Term("address", "avenue"), 1);
      phraseQuery.setSlop(0);
      
      phraseQuery = new PhraseQuery();
      phraseQuery.add(new Term("address", "st"), 0);
//      phraseQuery.add(new Term("address", "martin"), 1);
      phraseQuery.add(new Term("address", "church"), 5);
//      phraseQuery.add(new Term("address", "path"), 1);
      phraseQuery.setSlop(0);  // 0 hit
//      phraseQuery.setSlop(1);  // 0 hit
//      phraseQuery.setSlop(2);  // 0 hit
//      phraseQuery.setSlop(3);  // 0 hit
//      phraseQuery.setSlop(4);  // 1 hit
//      phraseQuery.setSlop(5);  // 1 hit
      
//      TopDocs topDocs = searcher.search(query, 10);
//      TopDocs topDocs = searcher.search(query, 10);
      TopDocs topDocs = searcher.search(phraseQuery, 10);
      
      StringBuilder sb = new StringBuilder("query: ").append(phraseQuery).append("\n");
      for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
        Document document = searcher.doc(scoreDoc.doc);
        sb.append("scoreDoc ").append(scoreDoc.doc).append(" address: ").append(document.get("address"));
      }
      if (topDocs.scoreDocs.length == 0) {
        sb.append("no hit\n");
      }
      System.out.println(sb.toString());
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  private static String getTokenStream(Analyzer analyzer, String fieldName, String text) throws IOException {
    StringBuilder sb = new StringBuilder("field name: ").append(fieldName).append("    text: ").append(text).append("\n\n");
    TokenStream   ts = analyzer.tokenStream(fieldName, text);
    ts.reset();
    while (ts.incrementToken()) {
      sb.append("token: ").append(ts.reflectAsString(false)).append("\n");
    }
    ts.end();
    try {
      return sb.toString();
    } finally {
      ts.close();
    }
  }
}
