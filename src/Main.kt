import kotlin.random.Random
import java.io.File

const val MENSAGEM_INVALIDA = "Resposta invalida."
const val MSG_GANHOU = "Ganhaste o jogo!"
const val MSG_PERDEU = "Perdeste o jogo!"
const val CHEAT_CODE = "abracadabra"

fun obtemCoordenadas(entrada: String, numLinhas: Int, numColunas: Int): Pair<Int, Int>? {
    val trimmed = entrada.trim().uppercase()
    if (trimmed.isEmpty() || trimmed.length < 2){
        return null
    }

    if (!trimmed[0].isDigit() || !trimmed.last().isLetter()){
        return null
    }

    val numeroStr = trimmed.dropLast(1)
    val letra = trimmed.last()

    val coluna = letra - 'A'
    if (coluna < 0 || coluna >= numColunas) {
        return null
    }

    var numero = 0
    var iProx = 0
    while (iProx < numeroStr.length) {
        val digito = numeroStr[iProx]
        if (digito < '0' || digito > '9'){
            return null
        }
        numero = numero * 10 + (digito - '0')
        iProx++
    }

    val linha = numero - 1
    if (linha < 0 || linha >= numLinhas) {
        return null
    }

    return Pair(linha, coluna)
}

fun validaCoordenadasDentroTerreno(coord: Pair<Int, Int>, numLinhas: Int, numColunas: Int): Boolean {
    val (linha, coluna) = coord
    return linha >= 0 && linha < numLinhas && coluna >= 0 && coluna < numColunas
}

fun validaMoveJogador(origem: Pair<Int, Int>, destino: Pair<Int, Int>): Boolean {
    val (origemLinha, origemColuna) = origem
    val (destinoLinha, destinoColuna) = destino

    if (origemLinha == destinoLinha && origemColuna == destinoColuna){
        return false
    }

    val difL = kotlin.math.abs(destinoLinha - origemLinha)
    val difC = kotlin.math.abs(destinoColuna - origemColuna)

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
    var coordenadaLinha = y1
    while (coordenadaLinha <= y2) {
        var coordenadaColuna = x1
        while (coordenadaColuna <= x2) {
            if (coordenadaLinha == linha && coordenadaColuna == coluna) {
                coordenadaColuna++
                continue
            }
            if (terreno[coordenadaLinha][coordenadaColuna].first == "*") contagem++
            coordenadaColuna++
        }
        coordenadaLinha++
    }
    return contagem
}

fun geraMatrizTerreno(numLinhas: Int, numColunas: Int, numMinas: Int): Array<Array<Pair<String, Boolean>>> {
    val terreno = Array(numLinhas) { Array(numColunas) { Pair(" ", false) } }

    terreno[0][0] = Pair("J", true)
    terreno[numLinhas - 1][numColunas - 1] = Pair("f", true)

    var minasColocadas = 0
    while (minasColocadas < numMinas) {
        val linha = Random.nextInt(numLinhas)
        val coluna = Random.nextInt(numColunas)

        if (terreno[linha][coluna].first == " " &&
            !(linha == 0 && coluna == 0) &&
            !(linha == numLinhas - 1 && coluna == numColunas - 1)
        ) {
            terreno[linha][coluna] = Pair("*", false)
            minasColocadas++
        }
    }
    return terreno
}

fun preencheNumMinasNoTerreno(terreno: Array<Array<Pair<String, Boolean>>>) {
    val linhas = terreno.size
    val colunas = terreno[0].size

    var coordenadaLinha = 0
    while (coordenadaLinha < linhas) {
        var coordenadaColuna = 0
        while (coordenadaColuna < colunas) {
            val atual = terreno[coordenadaLinha][coordenadaColuna].first
            if (atual != "*" && atual != "J" && atual != "f") {
                val num = contaMinasPerto(terreno, coordenadaLinha, coordenadaColuna)
                terreno[coordenadaLinha][coordenadaColuna] = Pair(
                    if (num == 0){
                        " "
                    } else{
                        num.toString()
                    }, terreno[coordenadaLinha][coordenadaColuna].second
                )
            }
            coordenadaColuna++
        }
        coordenadaLinha++
    }
}

fun revelaCelulasAoRedor(terreno: Array<Array<Pair<String, Boolean>>>, linha: Int, coluna: Int) {
    val limites = quadradoAVoltaDoPonto(linha, coluna, terreno.size, terreno[0].size)
    val (y1, x1) = limites.first
    val (y2, x2) = limites.second

    var coordenadaLinha = y1
    while (coordenadaLinha <= y2) {
        var coordenadaColuna = x1
        while (coordenadaColuna <= x2) {
            val conteudo = terreno[coordenadaLinha][coordenadaColuna].first
            if (conteudo != "*" && conteudo != "J" && conteudo != "f") {
                terreno[coordenadaLinha][coordenadaColuna] = Pair(conteudo, true)
            }
            coordenadaColuna++
        }
        coordenadaLinha++
    }
}

fun celulaTemNumeroMinasVisivel(terreno: Array<Array<Pair<String, Boolean>>>, linha: Int, coluna: Int): Boolean {
    val (conteudo, visivel) = terreno[linha][coluna]
    if (!visivel){
        return false
    }
    return conteudo.length == 1 && conteudo[0] in '1'..'8'
}

fun escondeMatriz(terreno: Array<Array<Pair<String, Boolean>>>) {
    var coordenadaLinha = 0
    val linhas = terreno.size
    val colunas = terreno[0].size

    while (coordenadaLinha < linhas) {
        var coordenadaColuna = 0
        while (coordenadaColuna < colunas) {
            val conteudo = terreno[coordenadaLinha][coordenadaColuna].first
            if (conteudo != "J" && conteudo != "f") {
                terreno[coordenadaLinha][coordenadaColuna] = Pair(conteudo, false)
            }
            coordenadaColuna++
        }
        coordenadaLinha++
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
            if (mostraLegenda) print("   ")
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
            "2" -> {

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

                // Pedir o ficheiro
                println("Qual o ficheiro de jogo a carregar?")
                val caminhoFicheiro = readln().trim()

                val terreno = lerFicheiroJogo(caminhoFicheiro, numLinhas, numColunas)

                if (terreno == null) {
                    println(MENSAGEM_INVALIDA)
                    continue
                }

                // Preenche os números
                preencheNumMinasNoTerreno(terreno)

                // Encontra a posição do J
                var posJogador = Pair(0, 0)
                var encontrouJ = false

                for (i in 0 until numLinhas) {
                    for (j in 0 until numColunas) {
                        if (terreno[i][j].first == "f") {
                            terreno[i][j] = Pair("f", true)
                            break
                        }
                    }
                }

                for (i in 0 until numLinhas) {
                    for (j in 0 until numColunas) {
                        if (terreno[i][j].first == "J") {
                            posJogador = Pair(i, j)
                            encontrouJ = true
                            break
                        }
                    }
                    if (encontrouJ) break
                }

                if (!encontrouJ) {
                    println(MENSAGEM_INVALIDA)
                    continue
                }

                // Torna o J visível (isso resolve o problema de não aparecer)
                terreno[posJogador.first][posJogador.second] = Pair("J", true)

                // Revela ao redor do J inicial
                revelaCelulasAoRedor(terreno, posJogador.first, posJogador.second)

                // Inicializa underlyingCurrent como espaço vazio (subjacente ao J)
                var underlyingCurrent = " "

                var tudoReveladoPermanente = false

                // Agora o ciclo de jogo (copiado/adaptado do jogarNovoJogo para funcionar)
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

                    // Perde
                    if (conteudoNovo == "*") {
                        terreno[novaL][novaC] = Pair("*", true)
                        criaTerreno(terreno, mostraLegenda, true)
                        println(MSG_PERDEU)
                        break
                    }

                    // Ganha (não move o J para f, só mostra o tabuleiro atual)
                    if (conteudoNovo == "f") {
                        criaTerreno(terreno, mostraLegenda, true)
                        println(MSG_GANHOU)
                        break
                    }

                    // Movimento normal
                    val eraVisivel = terreno[novaL][novaC].second

                    if (!tudoReveladoPermanente) {
                        escondeMatriz(terreno)
                    }

                    // Restaura a posição antiga para o conteúdo subjacente
                    terreno[posJogador.first][posJogador.second] = Pair(underlyingCurrent, false)

                    // Coloca o novo J e atualiza o conteúdo subjacente
                    underlyingCurrent = conteudoNovo
                    terreno[novaL][novaC] = Pair("J", true)
                    posJogador = destino

                    // Revela ao redor SOMENTE se a nova casa era desconhecida
                    if (!tudoReveladoPermanente && !eraVisivel) {
                        revelaCelulasAoRedor(terreno, novaL, novaC)
                    }
                }
                continue
            }
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

    var posJogador = Pair(0, 0)
    var underlyingCurrent = terreno[0][0].first  // conteúdo original da posição inicial

    // Coloca o jogador na posição inicial e revela ao redor
    terreno[0][0] = Pair("J", true)
    revelaCelulasAoRedor(terreno, 0, 0)

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

        // DERROTA - pisou numa mina
        if (conteudoNovo == "*") {
            terreno[novaL][novaC] = Pair("*", true)
            // Força a posição inicial para vazio se não for a posição atual
            if (posJogador != Pair(0, 0)) {
                terreno[0][0] = Pair(" ", true)
            }
            criaTerreno(terreno, mostraLegenda, true)
            println(MSG_PERDEU)
            break
        }

        // VITÓRIA - chegou à bandeira
        if (conteudoNovo == "f") {
            // Força a posição inicial para vazio se não for a posição atual
            if (posJogador != Pair(0, 0)) {
                terreno[0][0] = Pair(" ", true)
            }
            criaTerreno(terreno, mostraLegenda, true)
            println(MSG_GANHOU)
            break
        }

        // Movimento normal
        val eraVisivel = terreno[novaL][novaC].second

        if (!tudoReveladoPermanente) {
            escondeMatriz(terreno)
        }

        // Restaura a posição anterior (conteúdo original + invisível)
        terreno[posJogador.first][posJogador.second] = Pair(underlyingCurrent, false)

        // Move o jogador e guarda o novo conteúdo subjacente
        underlyingCurrent = conteudoNovo
        terreno[novaL][novaC] = Pair("J", true)
        posJogador = destino

        // Revelação ao redor se necessário
        if (!tudoReveladoPermanente) {
            if (!eraVisivel || conteudoNovo == " ") {
                revelaCelulasAoRedor(terreno, novaL, novaC)
            }
        }
    }
}

fun lerNumeroPositivo(mensagem: String): Int {
    while (true) {
        println(mensagem)
        val input = readln().trim()
        var valor = 0
        var valido = input.isNotEmpty()
        var coordenada = 0
        while (coordenada < input.length && valido) {
            if (input[coordenada] !in '0'..'9') {
                valido = false
            }
            else{
                valor = valor * 10 + (input[coordenada] - '0')
            }
            coordenada++
        }
        if (valido && valor > 0) {
            return valor
        }
        println(MENSAGEM_INVALIDA)
    }
}

fun validaNome(nome: String, minLetras: Int = 3): Boolean {
    var espacos = 0
    var contadorAtual = 0
    var primeiraDaPalavra = true
    var cLetra = 0

    while (cLetra < nome.length) {
        val c = nome[cLetra]
        if (c == ' ') {
            if (contadorAtual < minLetras){
                return false
            }
            espacos++
            contadorAtual = 0
            primeiraDaPalavra = true
        } else {
            if (primeiraDaPalavra) {
                if (c != c.uppercaseChar()){
                    return false
                }
                primeiraDaPalavra = false
            }
            contadorAtual++
        }
        cLetra++
    }

    if (contadorAtual < minLetras) return false
    espacos++

    return espacos == 2
}

fun lerFicheiroJogo(
    caminhoInput: String,
    linhasEsperadas: Int,
    colunasEsperadas: Int
): Array<Array<Pair<String, Boolean>>>? {
    // Tenta adicionar .txt se o utilizador não escreveu a extensão
    var caminho = caminhoInput.trim()
    if (!caminho.lowercase().endsWith(".txt")) {
        caminho += ".txt"
    }

    val ficheiro = File(caminho)

    if (!ficheiro.exists() || !ficheiro.isFile) {
        println("Ficheiro invalido")
        return null
    }

    val linhasFicheiro: List<String> = try {
        ficheiro.readLines()
            .filter { it.isNotBlank() }
            .map { it.trim() }
    } catch (e: Exception) {
        println(MENSAGEM_INVALIDA)
        return null
    }

    if (linhasFicheiro.size != linhasEsperadas) {
        println(MENSAGEM_INVALIDA)
        return null
    }

    val matriz = Array(linhasEsperadas) { i ->
        val partes = linhasFicheiro[i]
            .split(",")
            .map { it.trim() }

        if (partes.size != colunasEsperadas) {
            println(MENSAGEM_INVALIDA)
            return null
        }

        Array(colunasEsperadas) { j ->
            val valor = partes[j]
            when (valor) {
                "J", "*", "f", "" -> Pair(if (valor == "") " " else valor, false)
                else -> {
                    println(MENSAGEM_INVALIDA)
                    return null
                }
            }
        }
    }

    // Verificação extra: deve existir exatamente 1 J e 1 f
    var countJ = 0
    var countF = 0

    for (linha in matriz) {
        for (celula in linha) {
            when (celula.first) {
                "J" -> countJ++
                "f" -> countF++
            }
        }
    }

    if (countJ != 1 || countF != 1) {
        println(MENSAGEM_INVALIDA)
        return null
    }

    return matriz
}


fun revelaUmaMina(matrizTerreno: Array<Array<Pair<String, Boolean>>>) {
    val numLinhas = matrizTerreno.size
    if (numLinhas == 0) return
    val numColunas = matrizTerreno[0].size

    var coordenadaLinha = 0
    while (coordenadaLinha < numLinhas) {
        var coordenadaColuna = 0
        while (coordenadaColuna < numColunas) {
            if (matrizTerreno[coordenadaLinha][coordenadaColuna].first == "*" && !matrizTerreno[coordenadaLinha][coordenadaColuna].second) {
                matrizTerreno[coordenadaLinha][coordenadaColuna] = Pair("*", true)
                return  // Para na primeira mina oculta encontrada
            }
            coordenadaColuna++
        }
        coordenadaLinha++
    }
    // Se não encontrou nenhuma mina oculta, simplesmente retorna (não faz nada)
}

fun contaNumeroMinasNoCaminho(matrizTerreno: Array<Array<Pair<String, Boolean>>>, linha: Int, coluna: Int): Int {
    val numLinhas = matrizTerreno.size
    val numColunas = matrizTerreno[0].size

    var contagem = 0

    var coordenadaLinha = linha
    while (coordenadaLinha < numLinhas) {
        var coordenadaColuna = coluna
        while (coordenadaColuna < numColunas) {
            if (matrizTerreno[coordenadaLinha][coordenadaColuna].first == "*") {
                contagem++
            }
            coordenadaColuna++
        }
        coordenadaLinha++
    }

    return contagem
}
