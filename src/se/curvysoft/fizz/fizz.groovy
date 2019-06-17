import groovy.json.JsonOutput
import groovy.json.JsonSlurper

def base = 'https://api.noopschallenge.com'
def url = new URL("${base}/fizzbot")
def slurper = new JsonSlurper()
def data = slurper.parse(url.newReader())

while (data.nextQuestion) {
    url = new URL("${base}${data.nextQuestion}")
    data = slurper.parse(url.newReader())
    def answer
    if (data.rules && data.numbers) {
        answer = fizz(data)
    } else {
        answer = 'Java'
    }

    def con = url.openConnection()
    con.doOutput = true
    con.requestMethod = 'POST'
    con.setRequestProperty('Content-Type', 'application/json')
    def out = new OutputStreamWriter(con.outputStream)
    out.write(JsonOutput.toJson([answer: answer]))
    out.close()
    data = slurper.parseText(con.inputStream.text)
    if (data.grade) {
        println "${data.message} ${data.grade}"
    }
}

def fizz(input) {
    def numbers = (List<Integer>) input.numbers
    def rules = (List<Map<String, Object>>)input.rules
    def result = []
    numbers.each { nr ->
        def r = ''
        rules.each { rule ->
            if (nr % rule.number == 0) {
                r += rule.response
            }
        }
        if (r.length() == 0) {
            r = nr
        }
        result << r
    }
    return result.join(' ')
}