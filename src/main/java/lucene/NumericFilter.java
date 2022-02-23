package lucene;

import java.io.IOException;

import org.apache.lucene.analysis.FilteringTokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public class NumericFilter extends FilteringTokenFilter {
	private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);

	public NumericFilter(TokenStream in) {
		super(in);
	}

	@Override
	protected boolean accept() throws IOException {
		for (int i = 0; i < termAtt.toString().length(); i++) {
			// System.out.println(termAtt.toString().charAt(i));
			if (!Character.isDigit(termAtt.toString().charAt(i))) {
				return true;
			}
		}
		return false;
	}

}
