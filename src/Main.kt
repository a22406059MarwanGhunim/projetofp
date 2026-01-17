import kotlin.random.Random

const val MENSAGEM_INVALIDA = "Resposta invalida."
const val NAO_IMPLEMENTADO = "Não Implementado"
const val MSG_GANHOU = "Ganhaste o jogo!"
const val MSG_PERDEU = "Perdeste o jogo!"
const val CHEAT_CODE = "abracadabra"

fun obtemCoordenadas(entrada: String, numLinhas: Int, numColunas: Int): Pair<Int, Int>? {
    val trimmed = entrada.trim().uppercase()
    if (trimmed.isEmpty() || trimmed.length < 2) return null

    if (!trimmed[0].isDigit() || !trimmed.last().isLetter()) return null

    val numeroStr = trimmed.dropLast(1)
    val letra = trimmed.last()

    val coluna = letra - 'A'
    if (coluna < 0 || coluna >= numColunas) return null

    var numero = 0
    var i = 0
    while (i < numeroStr.length) {
        val digito = numeroStr[i]
        if (digito < '0' || digito > '9') return null
        numero = numero * 10 + (digito - '0')
        i++
    }

    val linha = numero - 1
    if (linha < 0 || linha >= numLinhas) return null

    return Pair(linha, coluna)
}

fun validaCoordenadasDentroTerreno(coord: Pair<Int, Int>, numLinhas: Int, numColunas: Int): Boolean {
    val (linha, coluna) = coord
    return linha >= 0 && linha < numLinhas && coluna >= 0 && coluna < numColunas
}

fun validaMoveJogador(origem: Pair<Int, Int>, destino: Pair<Int, Int>): Boolean {
    val (oL, oC) = origem
    val (dL, dC) = destino

    if (oL == dL && oC == dC) return false

    val difL = kotlin.math.abs(dL - oL)
    val difC = kotlin.math.abs(dC - oC)

    return difL <= 2 && difC <= 2
}

fun quadradoAVoltaDoPonto(linha: Int, coluna: Int, numLinhas: Int, numColunas: Int): Pair<Pair<Int, Int>, Pair<Int, Int>> {
    val y1 = maxOf(0, linha - 1)
    val x1 = maxOf(0, coluna - 1)
    val y2 = minOf(numLinhas - 1, linha + 1)
    val x2 = minOf(numColunas - 1, coluna + 1)
    return Pair(Pair(y1, x1), Pair(y2, x2))
}

fun contaMinasPerto(terreno: Array<Array<Pair<String, Boolean>>>, linha: Int, coluna: Int): Int {
    val limites = quadradoAVoltaDoPonto(linha, coluna, terreno.size, terreno[0].size)
    val (y1, x1) = limites.first
    val (y2, x2) = limites.second

    var contagem = 0
    var i = y1
    while (i <= y2) {
        var j = x1
        while (j <= x2) {
            if (i == linha && j == coluna) {
                j++
                continue
            }
            if (terreno[i][j].first == "*") contagem++
            j++
        }
        i++
    }
    return contagem
}

fun geraMatrizTerreno(numLinhas: Int, numColunas: Int, numMinas: Int): Array<Array<Pair<String, Boolean>>> {
    val terreno = Array(numLinhas) { Array(numColunas) { Pair(" ", false) } }

    terreno[0][0] = Pair("J", true)
    terreno[numLinhas - 1][numColunas - 1] = Pair("f", true)

    var minasColocadas = 0
    while (minasColocadas < numMinas) {
        val l = Random.nextInt(numLinhas)
        val c = Random.nextInt(numColunas)

        if (terreno[l][c].first == " " &&
            !(l == 0 && c == 0) &&
            !(l == numLinhas - 1 && c == numColunas - 1)
        ) {
            terreno[l][c] = Pair("*", false)
            minasColocadas++
        }
    }
    return terreno
}

fun preencheNumMinasNoTerreno(terreno: Array<Array<Pair<String, Boolean>>>) {
    val linhas = terreno.size
    val colunas = terreno[0].size

    var i = 0
    while (i < linhas) {
        var j = 0
        while (j < colunas) {
            val atual = terreno[i][j].first
            if (atual != "*" && atual != "J" && atual != "f") {
                val num = contaMinasPerto(terreno, i, j)
                terreno[i][j] = Pair(
                    if (num == 0) " " else num.toString(),
                    terreno[i][j].second
                )
            }
            j++
        }
        i++
    }
}

fun revelaCelulasAoRedor(terreno: Array<Array<Pair<String, Boolean>>>, linha: Int, coluna: Int) {
    val limites = quadradoAVoltaDoPonto(linha, coluna, terreno.size, terreno[0].size)
    val (y1, x1) = limites.first
    val (y2, x2) = limites.second

    var i = y1
    while (i <= y2) {
        var j = x1
        while (j <= x2) {
            val conteudo = terreno[i][j].first
            if (conteudo != "*" && conteudo != "J" && conteudo != "f") {
                terreno[i][j] = Pair(conteudo, true)
            }
            j++
        }
        i++
    }
}

fun celulaTemNumeroMinasVisivel(terreno: Array<Array<Pair<String, Boolean>>>, linha: Int, coluna: Int): Boolean {
    val (conteudo, visivel) = terreno[linha][coluna]
    if (!visivel) return false
    return conteudo.length == 1 && conteudo[0] in '1'..'8'
}

fun escondeMatriz(terreno: Array<Array<Pair<String, Boolean>>>) {
    var i = 0
    val linhas = terreno.size
    val colunas = terreno[0].size

    while (i < linhas) {
        var j = 0
        while (j < colunas) {
            val conteudo = terreno[i][j].first
            if (conteudo != "J" && conteudo != "f") {
                terreno[i][j] = Pair(conteudo, false)
            }
            j++
        }
        i++
    }
}

fun criaTerreno(
    terreno: Array<Array<Pair<String, Boolean>>>,
    mostraLegenda: Boolean = true,
    mostraTudo: Boolean = false
): String {
    val numLinhas = terreno.size
    if (numLinhas == 0) return ""

    val numColunas = terreno[0].size

    // Legenda superior
    if (mostraLegenda) {
        print("    ")  // 4 espaços
        var c = 0
        while (c < numColunas) {
            print(('A' + c))
            if (c < numColunas - 1) print("   ")  // 3 espaços
            c++
        }
        println()
    }

    var linha = 0
    while (linha < numLinhas) {
        if (mostraLegenda) {
            val numStr = (linha + 1).toString()
            if (numStr.length == 1) print(" ")
            print(numStr)
            print(" ")  // 1 espaço depois do número
        }

        var coluna = 0
        while (coluna < numColunas) {
            val (conteudo, visivel) = terreno[linha][coluna]
            val deveMostrar = mostraTudo || visivel
            val simbolo = if (deveMostrar) conteudo else " "

            print(" $simbolo ")

            if (coluna < numColunas - 1) print("|")
            coluna++
        }
        println()

        if (linha < numLinhas - 1) {
            if (mostraLegenda) print("    ")
            var sep = 0
            while (sep < numColunas) {
                print("---")
                if (sep < numColunas - 1) print("+")
                sep++
            }
            println()
        }
        linha++
    }

    return ""
}

fun main() {
    println("Bem vindo ao Campo DEISIado\n")

    while (true) {
        println("1 - Novo Jogo\n2 - Ler Jogo\n0 - Sair\n")

        when (readln().trim()) {
            "1" -> jogarNovoJogo()
            "2" -> println(NAO_IMPLEMENTADO)
            "0" -> return
            else -> println(MENSAGEM_INVALIDA)
        }
    }
}

fun jogarNovoJogo() {
    // Nome do jogador
    var nome = ""
    while (true) {
        println("Introduz o nome do jogador")
        val input = readln().trim()
        if (validaNome(input)) {
            nome = input
            break
        }
        println(MENSAGEM_INVALIDA)
    }

    // Mostrar legenda (s/n)
    var mostraLegenda = true
    while (true) {
        println("Mostrar legenda (s/n)")
        when (readln().trim().lowercase()) {
            "s" -> { mostraLegenda = true; break }
            "n" -> { mostraLegenda = false; break }
            else -> println(MENSAGEM_INVALIDA)
        }
    }

    // Linhas e colunas
    val numLinhas = lerNumeroPositivo("Quantas linhas?")
    val numColunas = lerNumeroPositivo("Quantas colunas?")

    // Minas
    var numMinas = 1
    while (true) {
        println("Quantas minas (ou enter para o valor por omissao)?")
        val input = readln().trim()
        if (input.isEmpty()) break

        var valor = 0
        var valido = true
        var k = 0
        while (k < input.length && valido) {
            if (input[k] !in '0'..'9') valido = false
            else valor = valor * 10 + (input[k] - '0')
            k++
        }

        val maxPermitido = numLinhas * numColunas - 2
        if (valido && valor >= 0 && valor <= maxPermitido) {
            numMinas = valor
            break
        }
        println(MENSAGEM_INVALIDA)
    }

    // Inicialização do tabuleiro
    var terreno = geraMatrizTerreno(numLinhas, numColunas, numMinas)
    preencheNumMinasNoTerreno(terreno)

    // Guarda conteúdos originais para restaurar
    val originalContent = Array(numLinhas) { i ->
        Array(numColunas) { j ->
            terreno[i][j].first
        }
    }

    // Revelação inicial
    revelaCelulasAoRedor(terreno, 0, 0)

    var posJogador = Pair(0, 0)
    var tudoReveladoPermanente = false

    while (true) {
        criaTerreno(terreno, mostraLegenda, tudoReveladoPermanente)

        println("Para onde quer ir? (ex: 2B, 3C, etc)")
        val entrada = readln().trim()

        if (entrada.lowercase() == CHEAT_CODE) {
            tudoReveladoPermanente = true
            var i = 0
            while (i < numLinhas) {
                var j = 0
                while (j < numColunas) {
                    terreno[i][j] = Pair(terreno[i][j].first, true)
                    j++
                }
                i++
            }
            continue
        }

        val destino = obtemCoordenadas(entrada, numLinhas, numColunas)
        if (destino == null || !validaCoordenadasDentroTerreno(destino, numLinhas, numColunas)) {
            println(MENSAGEM_INVALIDA)
            continue
        }

        if (!validaMoveJogador(posJogador, destino)) {
            println(MENSAGEM_INVALIDA)
            continue
        }

        val (novaL, novaC) = destino
        val conteudoNovo = terreno[novaL][novaC].first
        val eraVisivel = terreno[novaL][novaC].second

        // Esconde tudo antes do movimento (menos J e f)
        if (!tudoReveladoPermanente) {
            escondeMatriz(terreno)
        }

        // Restaura posição antiga (exceção para casa inicial)
        val conteudoOriginalAntigo = originalContent[posJogador.first][posJogador.second]
        terreno[posJogador.first][posJogador.second] = Pair(
            if (posJogador.first == 0 && posJogador.second == 0) " " else conteudoOriginalAntigo,
            true
        )

        // Coloca o novo J
        terreno[novaL][novaC] = Pair("J", true)
        posJogador = destino

        // Revela SEMPRE as 8 casas ao redor do novo J
        revelaCelulasAoRedor(terreno, novaL, novaC)

        // Perde (mina)
        if (conteudoNovo == "*") {
            terreno[novaL][novaC] = Pair("*", true)
            criaTerreno(terreno, mostraLegenda, true)
            println(MSG_PERDEU)
            break
        }

        // Ganha (não move J, mostra tudo revelado)
        if (conteudoNovo == "f") {
            criaTerreno(terreno, mostraLegenda, true)
            println(MSG_GANHOU)
            break
        }
    }
}

fun lerNumeroPositivo(mensagem: String): Int {
    while (true) {
        println(mensagem)
        val input = readln().trim()
        var valor = 0
        var valido = input.isNotEmpty()
        var k = 0
        while (k < input.length && valido) {
            if (input[k] !in '0'..'9') valido = false
            else valor = valor * 10 + (input[k] - '0')
            k++
        }
        if (valido && valor > 0) return valor
        println(MENSAGEM_INVALIDA)
    }
}

fun validaNome(nome: String, minLetras: Int = 3): Boolean {
    var espacos = 0
    var contadorAtual = 0
    var primeiraDaPalavra = true
    var i = 0

    while (i < nome.length) {
        val c = nome[i]
        if (c == ' ') {
            if (contadorAtual < minLetras) return false
            espacos++
            contadorAtual = 0
            primeiraDaPalavra = true
        } else {
            if (primeiraDaPalavra) {
                if (c != c.uppercaseChar()) return false
                primeiraDaPalavra = false
            }
            contadorAtual++
        }
        i++
    }

    if (contadorAtual < minLetras) return false
    espacos++

    return espacos == 2
}