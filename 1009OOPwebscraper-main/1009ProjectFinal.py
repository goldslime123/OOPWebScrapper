#@Author
#Program Done By: Cai Zhao an, Phua Kia Kai
#Window Layout, Displaying Data on Table, Done by: Cai Zhao An
#Tabs Layout and Pie Chart, Done by: Phua Kia Kai


import PySimpleGUI as gui
import sqlite3
import matplotlib.pyplot as plt
import os


conn = sqlite3.connect('crawler.db')
sql = conn.cursor()

sql.execute('''CREATE TABLE IF NOT EXISTS stockdb
           ([ID] INTEGER PRIMARY KEY,[Stock] text, [Source] text, [Date_Created] text, [Comment] text,[Sentiment] text)''')
sql.execute('''DELETE FROM stockdb''')

conn.commit()


gui.change_look_and_feel('BlueMono')
sourceSelection = ("GME", "AMC")
layout = [
    [gui.Text("Stocks")],
    [gui.Combo(sourceSelection, size=(40, 7), enable_events=True, key='-COMBO-')],
    [gui.Text("Source")],
    [gui.Checkbox('All', change_submits= True, default=False, key='-CheckAll-'),
     gui.Checkbox('Reddit', key=1),
     gui.Checkbox('Twitter', key=2),],
    [gui.Button("GO")]
]

# Create the window
window = gui.Window("Data Crawler Application", layout, margins=(300, 250))





# Create an event loop
while True:
    socialMedia = []
    event, values = window.read()
    #Check all boxes when selected

    if event == '-CheckAll-':
        if values['-CheckAll-'] is True:
            window.find_element('-CheckAll-').Update(text='Deselect', value=True)
            for x in range(1,3):
                window.Element(x).Update(True)
        #Uncheck all boxes
        else:
            window.find_element('-CheckAll-').Update(text='All', value=False)
            for x in range(1, 3):
                window.Element(x).Update(False)



    #Once everything is selected, time to start crawling
    if event == "GO":
        #If check all is select, the first item in the array will store it
        if values['-CheckAll-'] is True:
            socialMedia.append('all')
        #Else assign the key of the social media into the array
        else:
            for x in range(1,3):
                print(window.find_element(x))
                if values[x] is True:
                    socialMedia.append(x)

        #Check for which stocks is selected
        if values['-COMBO-'] != '':
            combo = values['-COMBO-']  # use the combo key
            break

    if event == gui.WIN_CLOSED:
        print("Deleting...")
        sql.execute('''DELETE FROM stockdb''')
        conn.close()
        quit()


window.close()

#print(combo) #Which stock is chosen
#print(socialMedia) #key of social media

if len(socialMedia)>1:
    socialMedia = "all"
else:
    socialMedia = socialMedia[0]





###Call Crawler
os.system("java -classpath C:/Users/kiaka/Onedrive/Desktop/1009Final/crawler.jar MainProgram " + str(socialMedia) + " " + combo) #(java -classpath -location- -mainclass-)
###Call Crawler










### If User choose Reddit or Twitter, run these queries
if socialMedia == 1 or socialMedia == 2:

    sql.execute('''SELECT * FROM stockdb''')
    sentimentTotal = sql.fetchone()
    sentimentTotal = sentimentTotal[0]

    sql.execute('''SELECT COUNT(*) FROM stockdb WHERE Sentiment = "Positive"''')
    sentimentPositive = sql.fetchone()
    sentimentPositive = sentimentPositive[0]/sentimentTotal

    sql.execute('''SELECT COUNT(*) FROM stockdb WHERE Sentiment = "Super Positive"''')
    sentimentSuperPositive = sql.fetchone()
    sentimentSuperPositive = sentimentSuperPositive[0]/sentimentTotal

    sql.execute('''SELECT COUNT(*) FROM stockdb WHERE Sentiment = "Neutral"''')
    sentimentNeutral = sql.fetchone()
    sentimentNeutral = sentimentNeutral[0]/sentimentTotal

    sql.execute('''SELECT COUNT(*) FROM stockdb WHERE Sentiment = "Negative"''')
    sentimentNegative = sql.fetchone()
    sentimentNegative = sentimentNegative[0]/sentimentTotal

    sql.execute('''SELECT COUNT(*) FROM stockdb WHERE Sentiment = "Super Negative"''')
    sentimentSuperNegative = sql.fetchone()
    sentimentSuperNegative = sentimentSuperNegative[0]/sentimentTotal

    labels=[]
    sizes =[]

    ###If Values are not 0, append to the list that is suppose to display
    if sentimentPositive != 0:
        labels.append("Positive")
        sizes.append(sentimentPositive)
    if sentimentSuperPositive !=0:
        labels.append("Super Positive")
        sizes.append(sentimentSuperPositive)
    if sentimentNeutral != 0:
        labels.append("Neutral")
        sizes.append(sentimentNeutral)
    if sentimentNegative != 0:
        labels.append("Negative")
        sizes.append(sentimentNegative)
    if sentimentSuperNegative !=0:
        labels.append("Super Negative")
        sizes.append(sentimentSuperNegative)

    ###Creating the Pie chart and saving as a image
    fig1, ax1 = plt.subplots()
    ax1.pie(sizes, labels=labels, autopct='%1.1f%%',
            shadow=True, startangle=90)
    ax1.axis('equal')  # Equal aspect ratio ensures that pie is drawn as a circle.
    plt.savefig('Sentiments.png')
    ###Creating the Pie chart and saving as a image

    ###Querying comment and sentiment to be displayed in a table
    sql.execute('''SELECT Comment,Sentiment FROM stockdb ORDER BY Sentiment''')
    queryComments = sql.fetchall()
    data = []
    for rows in queryComments:
        data.append([rows[0],rows[1]])

    ###Adding the rows and column into the table element
    header_list = ['Comments', 'Sentiment']
    table = gui.Table(values=data,
                        max_col_width=50,
                        headings=header_list,
                        auto_size_columns=True,
                        num_rows=100,
                        justification='center',
                        alternating_row_color='lightblue',
                        display_row_numbers=True,
                        key='tablet')
    tab1_layout = [[gui.Text(combo,size="90")], [gui.Image(r'Sentiments.png')],[table]]
    source = ""

    ###Checking reddit or twitter chosen display as tab header
    if socialMedia == 1:
        source = source + "Reddit"
    elif socialMedia == 2:
        source = source + "Twitter"

    ###Put into a tab group layout which later is put into col
    tab_group_layout = [[gui.Tab(source, tab1_layout, font='Courier 15', key='-TAB1-')]]
else:
    ###Else which means user choose 2(All) sources

    ########Total sentiment for reddit and twitter combined########
    sql.execute('''SELECT COUNT(*) FROM stockdb''')
    sentimentTotal = sql.fetchone()
    sentimentTotal = sentimentTotal[0]

    ########Total sentiment for reddit alone########
    sql.execute('''SELECT COUNT(*) FROM stockdb WHERE Source="Reddit"''')
    redditSentimentTotal = sql.fetchone()
    redditSentimentTotal = redditSentimentTotal[0]

    ########Total sentiment for twitter alone########
    sql.execute('''SELECT COUNT(*) FROM stockdb WHERE Source="Twitter"''')
    twitterSentimentTotal = sql.fetchone()
    twitterSentimentTotal = twitterSentimentTotal[0]


    ########Positive sentiment for reddit and twitter combined########
    sql.execute('''SELECT COUNT(*) FROM stockdb WHERE Sentiment = "Positive"''')
    sentimentPositive = sql.fetchone()
    sentimentPositive = (sentimentPositive[0] / sentimentTotal)*100

    ########Positive sentiment for reddit alone########
    sql.execute('''SELECT COUNT(*) FROM stockdb WHERE Sentiment = "Positive" AND Source = "Reddit"''')
    redditSentimentPositive = sql.fetchone()
    redditSentimentPositive = (redditSentimentPositive[0] / redditSentimentTotal)*100

    ########Positive sentiment for twitter alone########
    sql.execute('''SELECT COUNT(*) FROM stockdb WHERE Sentiment = "Positive" AND Source = "Twitter"''')
    twitterSentimentPositive = sql.fetchone()
    twitterSentimentPositive = (twitterSentimentPositive[0] / twitterSentimentTotal)*100

    ########Super Positive sentiment for reddit and twitter combined########
    sql.execute('''SELECT COUNT(*) FROM stockdb WHERE Sentiment = "Super Positive"''')
    sentimentSuperPositive = sql.fetchone()
    sentimentSuperPositive = (sentimentSuperPositive[0] / sentimentTotal)*100

    ########Super Positive sentiment for reddit alone########
    sql.execute('''SELECT COUNT(*) FROM stockdb WHERE Sentiment = "Super Positive" AND Source="Reddit"''')
    redditSentimentSuperPositive = sql.fetchone()
    redditSentimentSuperPositive = (redditSentimentSuperPositive[0] / redditSentimentTotal)*100

    ########Super Positive sentiment for twitter alone########
    sql.execute('''SELECT COUNT(*) FROM stockdb WHERE Sentiment = "Super Positive" AND Source="Twitter"''')
    twitterSentimentSuperPositive = sql.fetchone()
    twitterSentimentSuperPositive = (twitterSentimentSuperPositive[0] / twitterSentimentTotal)*100


    ########Neutral for reddit and twitter combined########
    sql.execute('''SELECT COUNT(*) FROM stockdb WHERE Sentiment = "Neutral"''')
    sentimentNeutral = sql.fetchone()
    sentimentNeutral = (sentimentNeutral[0] / sentimentTotal)*100

    ########Neutral for reddit alone########
    sql.execute('''SELECT COUNT(*) FROM stockdb WHERE Sentiment = "Neutral" AND Source="Reddit"''')
    redditSentimentNeutral = sql.fetchone()
    redditSentimentNeutral = (redditSentimentNeutral[0] / redditSentimentTotal)*100

    ########Neutral for twitter alone########
    sql.execute('''SELECT COUNT(*) FROM stockdb WHERE Sentiment = "Neutral" AND Source="Twitter"''')
    twitterSentimentNeutral = sql.fetchone()
    twitterSentimentNeutral = (twitterSentimentNeutral[0] / twitterSentimentTotal)*100

    ########Negative for reddit and twitter combined########
    sql.execute('''SELECT COUNT(*) FROM stockdb WHERE Sentiment = "Negative"''')
    sentimentNegative = sql.fetchone()
    sentimentNegative = (sentimentNegative[0] / sentimentTotal)*100

    ########Negative for reddit alone########
    sql.execute('''SELECT COUNT(*) FROM stockdb WHERE Sentiment = "Negative" AND Source="Reddit"''')
    redditSentimentNegative = sql.fetchone()
    redditSentimentNegative = (redditSentimentNegative[0] / redditSentimentTotal)*100

    ########Negative for twitter alone########
    sql.execute('''SELECT COUNT(*) FROM stockdb WHERE Sentiment = "Negative" AND Source="Twitter"''')
    twitterSentimentNegative = sql.fetchone()
    twitterSentimentNegative = (twitterSentimentNegative[0] / twitterSentimentTotal)*100

    ########Super Negative for reddit and twitter combined########
    sql.execute('''SELECT COUNT(*) FROM stockdb WHERE Sentiment = "Super Negative"''')
    sentimentSuperNegative = sql.fetchone()
    sentimentSuperNegative = (sentimentSuperNegative[0] / sentimentTotal)*100

    ########Super Negative for reddit alone########
    sql.execute('''SELECT COUNT(*) FROM stockdb WHERE Sentiment = "Super Negative" AND Source="Reddit"''')
    redditSentimentSuperNegative = sql.fetchone()
    redditSentimentSuperNegative = (redditSentimentSuperNegative[0] / redditSentimentTotal)*100

    ########Super Negative for twitter alone########
    sql.execute('''SELECT COUNT(*) FROM stockdb WHERE Sentiment = "Super Negative" AND Source="Twitter"''')
    twitterSentimentSuperNegative = sql.fetchone()
    twitterSentimentSuperNegative = (twitterSentimentSuperNegative[0] / twitterSentimentTotal)*100

    ####Creating Label and size for non 0 values for twitter
    twitterLabels = []
    twitterSizes = []
    if twitterSentimentPositive != 0:
        twitterLabels.append("Positive")
        twitterSizes.append(int(twitterSentimentPositive))
    if twitterSentimentSuperPositive != 0:
        twitterLabels.append("Super Positive")
        twitterSizes.append(int(twitterSentimentSuperPositive))
    if twitterSentimentNeutral != 0:
        twitterLabels.append("Neutral")
        twitterSizes.append(int(twitterSentimentNeutral))
    if twitterSentimentNegative != 0:
        twitterLabels.append("Negative")
        twitterSizes.append(int(twitterSentimentNegative))
    if twitterSentimentSuperNegative != 0:
        twitterLabels.append("Super Negative")
        twitterSizes.append(int(twitterSentimentSuperNegative))

    ###Creating PIE chart and saving as image
    fig1, ax1 = plt.subplots()
    ax1.pie(twitterSizes, labels=twitterLabels, autopct='%1.1f%%',
            shadow=True, startangle=90)
    ax1.axis('equal')  # Equal aspect ratio ensures that pie is drawn as a circle.
    plt.savefig('SentimentsTwitter.png')
    sql.execute('''SELECT Comment,Sentiment FROM stockdb WHERE Source="Twitter" ORDER BY Sentiment''')
    queryComments = sql.fetchall()
    dataTwitter = []
    for rows in queryComments:
        dataTwitter.append([rows[0],rows[1]])

    ###Adding the rows into the table elements
    header_listTwitter = ['Comments', 'Sentiment']
    tableTwitter = gui.Table(values=dataTwitter,
                      max_col_width=50,
                      headings=header_listTwitter,
                      auto_size_columns=True,
                      num_rows=100,
                      justification='center',
                      alternating_row_color='lightblue',
                      display_row_numbers=True,
                      key='tablet1')
    tab1_layout = [[gui.Text(combo, size="90")], [gui.Image(r'SentimentsTwitter.png')], [tableTwitter]]

    ####Creating Label and size for non 0 values for reddit
    redditLabels = []
    redditSizes = []
    if redditSentimentPositive != 0:
        redditLabels.append("Positive")
        redditSizes.append(redditSentimentPositive)
    if redditSentimentSuperPositive != 0:
        redditLabels.append("Super Positive")
        redditSizes.append(redditSentimentSuperPositive)
    if redditSentimentNeutral != 0:
        redditLabels.append("Neutral")
        redditSizes.append(redditSentimentNeutral)
    if redditSentimentNegative != 0:
        redditLabels.append("Negative")
        redditSizes.append(redditSentimentNegative)
    if redditSentimentSuperNegative != 0:
        redditLabels.append("Super Negative")
        redditSizes.append(redditSentimentSuperNegative)

    ###Creating PIE chart and saving as image
    fig2, ax2 = plt.subplots()
    ax2.pie(redditSizes, labels=redditLabels, autopct='%1.1f%%',
            shadow=True, startangle=90)
    ax2.axis('equal')  # Equal aspect ratio ensures that pie is drawn as a circle.
    plt.savefig('SentimentsReddit.png')
    sql.execute('''SELECT Comment,Sentiment FROM stockdb WHERE Source="Reddit" ORDER BY Sentiment''')
    queryComments = sql.fetchall()
    dataReddit = []
    for rows in queryComments:
        dataReddit.append([rows[0],rows[1]])

    ###Adding the rows into the table elements
    header_listReddit = ['Comments', 'Sentiment']
    tableReddit = gui.Table(values=dataReddit,
                             max_col_width=50,
                             headings=header_listReddit,
                             auto_size_columns=True,
                             num_rows=100,
                             justification='center',
                             alternating_row_color='lightblue',
                             display_row_numbers=True,
                             key='tablet2')
    tab2_layout = [[gui.Text(combo, size="90")], [gui.Image(r'SentimentsReddit.png')], [tableReddit]]

    ####Creating Label and size for non 0 values for both for both sources
    labels=[]
    sizes =[]
    if sentimentPositive != 0:
        labels.append("Positive")
        sizes.append(sentimentPositive)
    if sentimentSuperPositive !=0:
        labels.append("Super Positive")
        sizes.append(sentimentSuperPositive)
    if sentimentNeutral != 0:
        labels.append("Neutral")
        sizes.append(sentimentNeutral)
    if sentimentNegative != 0:
        labels.append("Negative")
        sizes.append(sentimentNegative)
    if sentimentSuperNegative !=0:
        labels.append("Super Negative")
        sizes.append(sentimentSuperNegative)

    ###Creating PIE chart and saving as image
    fig3, ax3 = plt.subplots()
    ax3.pie(sizes, labels=labels, autopct='%1.1f%%',
            shadow=True, startangle=90)
    ax3.axis('equal')  # Equal aspect ratio ensures that pie is drawn as a circle.
    plt.savefig('Sentiments.png')
    sql.execute('''SELECT Comment,Source,Sentiment FROM stockdb ORDER BY Sentiment,Source''')
    queryComments = sql.fetchall()
    data = []
    for rows in queryComments:
        data.append([rows[0],rows[1],rows[2]])

    ###Adding the rows into the table elements
    header_list = ['Comments', 'Source', 'Sentiment']
    table = gui.Table(values=data,
                             max_col_width=50,
                             headings=header_list,
                             auto_size_columns=True,
                             num_rows=100,
                             justification='center',
                             alternating_row_color='lightblue',
                             display_row_numbers=True,
                             key='tablet3')
    tab3_layout = [[gui.Text(combo, size="90")], [gui.Image(r'Sentiments.png')], [table]]

    ###Now put all the tabs into a group tab layout for later col use
    tab_group_layout = [[gui.Tab("Twitter", tab1_layout, font='Courier 15', key='-TAB1-'),#]]
                        gui.Tab("Reddit", tab2_layout, key='-TAB2-'),
                        gui.Tab("All", tab3_layout, key='-TAB3-')]]
######GUI

# The window layout - defines the entire window
col = [[gui.TabGroup(tab_group_layout,
                       enable_events=True,
                       key='-TABGROUP-')]]

###Scrollable Tab Creation
layout = [[gui.Column(col,size=(700,500),scrollable=True)]]

### Making the window pop up
secondWin = gui.Window('Data Crawler Application', layout)


while True:
        event, values = secondWin.read()
        if event == gui.WIN_CLOSED:
            print("Deleting...")
            sql.execute('''DELETE FROM stockdb''')
            conn.close()
            if socialMedia!="all":
                os.remove("Sentiments.png")
            else:
                os.remove("Sentiments.png")
                os.remove("SentimentsReddit.png")
                os.remove("SentimentsTwitter.png")
            exit()

secondWin.close()



