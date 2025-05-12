import json
import os

WATCHLIST_FILE = "watchlist.json"

def load_watchlist():
    if not os.path.exists(WATCHLIST_FILE):
        return []
    with open(WATCHLIST_FILE, "r") as f:
        return json.load(f)

def save_watchlist(watchlist):
    with open(WATCHLIST_FILE, "w") as f:
        json.dump(watchlist, f)

def add_stock(ticker):
    ticker = ticker.upper()
    watchlist = load_watchlist()
    if ticker not in watchlist:
        watchlist.append(ticker)
        save_watchlist(watchlist)
        print(f"{ticker} added to watchlist.")
    else:
        print(f"{ticker} is already in your watchlist.")

def remove_stock(ticker):
    ticker = ticker.upper()
    watchlist = load_watchlist()
    if ticker in watchlist:
        watchlist.remove(ticker)
        save_watchlist(watchlist)
        print(f"{ticker} removed from watchlist.")
    else:
        print(f"{ticker} not found in watchlist.")
