
from watchlist import load_watchlist, add_stock, remove_stock
from fetcher import get_stock_info, plot_stock_chart
from news import fetch_news

def menu():
    print("\n--- FinSight ---")
    print("1. View Watchlist")
    print("2. Add Stock")
    print("3. Remove Stock")
    print("4. Analyze Stock")
    print("5. Get Stock News")
    print("6. Exit")

def main():
    while True:
        menu()
        choice = input("Choose an option: ")
        if not choice:
            print("No input. Try again.")
            continue
        elif choice == "1":
            print(load_watchlist())
        elif choice == "2":
            add_stock(input("Enter ticker: "))
        elif choice == "3":
            remove_stock(input("Enter ticker: "))
        elif choice == "4":
            ticker = input("Enter ticker to analyze: ")
            get_stock_info(ticker)
            plot_stock_chart(ticker)
        elif choice == "5":
            ticker = input("Enter ticker for news: ")
            fetch_news(ticker)
        elif choice == "6":
            break
        
        else:
            print("Invalid choice. Try again.")

if __name__ == "__main__":
    main()