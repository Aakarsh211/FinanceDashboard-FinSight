import os
from newsapi import NewsApiClient
from textblob import TextBlob
from dotenv import load_dotenv

load_dotenv()

newsapi = NewsApiClient(api_key=os.getenv("NEWS_API_KEY"))

def fetch_news(ticker):
    company_name = ticker.upper()
    print(f"\n--- News for {company_name} ---")
    all_articles = newsapi.get_everything(q=company_name, language='en', sort_by='relevancy', page_size=5)

    if not all_articles['articles']:
        print("No recent news found.")
        return

    for i, article in enumerate(all_articles['articles']):
        title = article['title']
        print(f"{i+1}. {title}")

        # Sentiment
        sentiment = TextBlob(title).sentiment.polarity
        if sentiment > 0.2:
            label = "Positive"
        elif sentiment < -0.2:
            label = "Negative"
        else:
            label = "Neutral"

        print(f"   ➤ Sentiment: {label} ({sentiment:.2f})\n")
