package com.umlet.language.sorting;

import java.util.List;

import com.umlet.language.SortableElement;

public class AlphabetLayout extends Layout {

	@Override
	public void layout(List<SortableElement> elements) {
		super.simpleLayout(new AlphabetSorter(), elements);
	}
}
