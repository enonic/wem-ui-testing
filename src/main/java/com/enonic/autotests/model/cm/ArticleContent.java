package com.enonic.autotests.model.cm;


public class ArticleContent extends BaseAbstractContent
{

	protected ArticleContent( Builder<?> builder )
	{
		super(builder);
		
	}
	private String title;
	private String text;
	
	public static abstract class Builder<T extends ArticleContent> extends BaseAbstractContent.Builder<T>
	{

		public abstract T build();
	}

	public static Builder<?> builder()
	{
		return new Builder<ArticleContent>()
		{
			@Override
			public ArticleContent build()
			{
				return new ArticleContent(this);
			}
		};
	}
}
