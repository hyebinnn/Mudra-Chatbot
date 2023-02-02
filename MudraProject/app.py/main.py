# -*- coding: utf-8 -*-
# app.py
from flask import Flask, render_template, request, jsonify
import Algo_aano

# flask 객체 인스턴스 생성
app = Flask(__name__)
app.config['JSON_AS_ASCII'] = False


@app.route('/')   # 접속하는 url
def index():
    return 'Safety Login'

answers = []


@app.route('/getAnswer', methods = ['POST'])
def getAnswer():
    answer = request.get_json()     # json 받아오기
    result = Algo_aano.runningApp(answer)

    return jsonify(result)      # 받아온 데이터 전송


@app.errorhandler(404)
def page_not_found(error):
    app.logger.error(error)
    return render_template('page_not_found.html'), 404


if __name__=="__main__":
    app.run()
    # host 등을 직접 지정하고 싶다면
    #app.run(host="173.30.1.22", port="5000", debug=True)