package jp.example.fileviewer_qc;

import java.text.Collator;
import java.util.Comparator;

public class Sort {
	//名前昇順
		public final static Comparator<CustomData> sDisplayNameComparator = new Comparator<CustomData>() {
			private final Collator collator = Collator.getInstance();

			public int compare(CustomData data1, CustomData data2) {
				// TODO Auto-generated method stub
				return collator.compare(data1.getfileName(), data2.getfileName());
			}
		};
}
