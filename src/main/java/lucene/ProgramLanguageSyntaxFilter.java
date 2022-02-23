package lucene;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.FilteringTokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public class ProgramLanguageSyntaxFilter extends FilteringTokenFilter {
	private final CharArraySet stopWords;
	private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);

	public ProgramLanguageSyntaxFilter(TokenStream input, CharArraySet stopWords) {
		super(input);
		this.stopWords = stopWords;
		// TODO Auto-generated constructor stub
	}

	public boolean accept() {
		return !stopWords.contains(termAtt.buffer(), 0, termAtt.length());
	}
}
