import json
import random
import pandas as pd
import sys
import io
import os

os.environ['TF_CPP_MIN_LOG_LEVEL'] = '2'
import firebase_admin
from firebase_admin import credentials
from firebase_admin import db
from konlpy.tag import Hannanum
# from pykospacing import Spacing
import re
from collections import OrderedDict
from properties import url, certificate

def getFirebase():
    db_url = url
    cred = credentials.Certificate(certificate)
    default_app = firebase_admin.initialize_app(cred, {'databaseURL': db_url})
    df = pd.DataFrame(db.reference('한국영화').get())
    return df


def getSeverdata(answer_user):
    # 예시
    ans_df = pd.DataFrame(answer_user)
    od = OrderedDict(answer_user)

    def hasNumber(stringVal):
        return any(elem.isdigit() for elem in stringVal)

    for i in range(4):
        if hasNumber(ans_df.loc[0][i]) == True:
            od.move_to_end(ans_df.columns[i])

    answer_df = pd.DataFrame(od)

    nlpAnswer_list = []
    for i in range(3):
        new_answer = answer_df.loc[0][i].replace(" ", '')
        # spacing=Spacing()
        # kospacing_answer = spacing(new_answer)
        hannanum = Hannanum()
        a = hannanum.nouns(new_answer)
        nlpAnswer_list.append(a)

    grade = answer_df.loc[0][3]
    year = answer_df.loc[0][4]

    if grade.find("전체") >= 0:
        grade_result = "전체"
    else:
        grade_result = re.sub(r'[^0-9]', '', grade)

    year_result = re.sub(r'[^0-9]', '', year)
    nlpAnswer_list.append(grade_result)
    nlpAnswer_list.append(year_result)
    # [['유선동'], ['공포','스릴러'], ['정은지'], '15', '2019']
    return nlpAnswer_list


def searchingAlgorithm(movie, answer):
    tmp = movie[movie['director'].str.contains(answer.iloc[0]['감독'], na=False)]
    tmp = tmp[tmp['grade'].str.contains(answer.iloc[0]['등급'], na=False)]
    tmp = tmp[tmp['genre'].str.contains(answer.iloc[0]['장르'], na=False)]
    tmp = tmp[tmp['actors'].str.contains(answer.iloc[0]['주연배우'], na=False)]
    tmp = tmp[tmp['dates'].str.contains(answer.iloc[0]['개봉년도'], na=False)]
    name = tmp.iloc[0]['movie_name']
    # nameJson = {
    #     '정답': name
    # }
    return name


def runningApp(answer_user):
    sys.stdout = io.TextIOWrapper(sys.stdout.detach(), encoding='utf-8')  # 한글깨짐 방지
    sys.stderr = io.TextIOWrapper(sys.stderr.detach(), encoding='utf-8')  # 한글깨짐 방지
    movie = getFirebase()
    answer = getSeverdata(answer_user)
    result = searchingAlgorithm(movie, answer)
    return result


if __name__ == '__main__':
    answer_user = {  # 사용자에게 받은 대답( 안스에서 넘어온거 )
        '감독': ['유선동 입니다.'],
        '등급': ['15세 관람가'],
        '장르': ['공포, 스릴러'],
        '개봉년도': ['2019년도 일껄?'],
        '주연배우': ['정은지 입니다']

    }
    answer = getSeverdata(answer_user)
    print(answer)
