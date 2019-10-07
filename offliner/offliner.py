#!/bin/python

from itertools import cycle, tee, izip
import requests
from bs4 import BeautifulSoup
import json

url_base = 'http://www.etymonline.com'
url_listing = '/search?q='
url_param_page = '&page='

letters = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
           'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z']


def letter_loop():
    print "["
    for letter in letters:
        page_num = 0
        url = url_base + url_listing + letter + url_param_page + str(page_num)
        request = requests.get(url)
        page_html = request.text
        page_object = BeautifulSoup(page_html)

        # get other pages
        pagination = page_object.body.find("div", class_="paging")
        if pagination:
            for link in pagination.find_all("a"):
                for word, etymology in handle_page(url_base + link['href']):
                    print_json_etym(word, etymology)
        else:
            for word, etymology in handle_page(url_base + link['href']):
                print_json_etym(word, etymology)
    print "]"

def print_json_etym(word, etymology):
    word = word.text.replace("\n", " ").strip()
    etymology = etymology.text.replace("\n", " ").strip()
    print json.dumps({"word": word, "etymology": etymology}, sort_keys=False), ","

#http://stackoverflow.com/a/2167877/1200542
def pairwise(seq):
    a, b = tee(seq)
    next(b)
    return izip(a, b)

def handle_page(url):
    request = requests.get(url)
    page_object = BeautifulSoup(request.text)
    definitions = page_object.body.find("div", id="dictionary").dl.find_all(True, recursive=False)
    definitions = iter(definitions)
    for word in definitions:
        etymology = next(definitions)
        yield (word, etymology)
        # print word.text
        # print etymology.text
    # exit()



if __name__ == "__main__":
    letter_loop()
