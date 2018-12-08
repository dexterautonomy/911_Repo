package com.hingebridge.model;

public class PagerModel
{
    private int endPage;
    private int startPage;
    private final int buttonsToShow = 7;
    
    public PagerModel(int totalPages, int currentPage)
    {
        int halfPagesToShow = getButtonsToShow() / 2;
        
	if (totalPages <= getButtonsToShow())
	{
            setStartPage(1);
            setEndPage(totalPages);
        }
	else if (currentPage - halfPagesToShow <= 0)
	{
            setStartPage(1);
            setEndPage(getButtonsToShow());
        }
	else if (currentPage + halfPagesToShow == totalPages)
	{
            setStartPage(currentPage - halfPagesToShow);
            setEndPage(totalPages);
        }
	else if (currentPage + halfPagesToShow > totalPages)
	{
            setStartPage(totalPages - getButtonsToShow() + 1);
            setEndPage(totalPages);
        }
	else
	{
            setStartPage(currentPage - halfPagesToShow);
            setEndPage(currentPage + halfPagesToShow);
        }
    }
    
    public int getButtonsToShow()
    {
        return buttonsToShow;
    }
    
    public void setStartPage(int startPage)
    {
        this.startPage = startPage;
    }
    
    public int getStartPage()
    {
        return startPage;
    }
    
    public void setEndPage(int endPage)
    {
        this.endPage = endPage;
    }
    
    public int getEndPage()
    {
        return endPage;
    }
}