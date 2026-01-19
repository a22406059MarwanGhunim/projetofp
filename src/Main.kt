import java.io.File
import kotlin.random.Random

const val MENSAGEM_INVALIDA = "Resposta invalida."
const val MSG_GANHOU = "\nGanhaste o jogo!"
const val MSG_PERDEU = "\nPerdeste o jogo!"
const val CHEAT_CODE = "abracadabra"

fun criaMenu(): String? {
    return "\nBem vindo ao Campo DEISIado\n\n1 - Novo Jogo\n2 - Ler Jogo\n0 - Sair\n"
}

fun validaNumeroDeMinas(linhas: Int, colunas: Int, numMinas: Int): Boolean {
    if (numMinas <= 0){
        return false
    }
    val celulasLivres = linhas * colunas - 2
    return numMinas <= celulasLivres
}

fun calculaNumeroDeMinas(linhas: Int, colunas: Int): Int {
    val livres = linhas * colunas - 2
    return when {
        livres <= 1 -> 1
        livres <= 5 -> 2
        livres <= 10 -> 3
        livres <= 20 -> 6
        livres <= 50 -> 10
        else -> 15
    }
}

fun criaLegenda(colunas: Int): String {
    var legenda = ""
    var coluna = 0
    while (coluna < colunas) {
        legenda += ('A' + coluna)
        if (coluna < colunas - 1){
            legenda += "   "
        }
        coluna++
    }
    return legenda
}

fun revelaMatriz(matrizTerreno: Array<Array<Pair<String, Boolean>>>, linha: Int, coluna: Int) {
    val limites = quadradoAVoltaDoPonto(linha, coluna, matrizTerreno.size, matrizTerreno[0].size)
    val (y1, x1) = limites.first
    val (y2, x2) = limites.second

    var coordenadaLinha = y1
    while (coordenadaLinha <= y2) {
        var coordenadaColuna = x1
        while (coordenadaColuna <= x2) {
            val conteudo = matrizTerreno[coordenadaLinha][coordenadaColuna].first
            if (conteudo != "*" && conteudo != "J" && conteudo != "f") {
                matrizTerreno[coordenadaLinha][coordenadaColuna] = Pair(conteudo, true)
            }
            coordenadaColuna++
        }
        coordenadaLinha++
    }
}

fun validaTerreno(terreno: Array<Array<Pair<String, Boolean>>>): Boolean {
    if (terreno.isEmpty() || terreno[0].isEmpty()) {
        return false
    }

    val numLinhas = terreno.size
    val numColunas = terreno[0].size

    // Verifica primeira posição (0,0) é "J"
    if (terreno[0][0].first != "J") {
        return false
    }

    // Verifica última posição (numLinhas-1, numColunas-1) é "f"
    if (terreno[numLinhas - 1][numColunas - 1].first != "f") {
        return false
    }

    // Conta quantos "J" e "f" existem no tabuleiro inteiro
    var countJ = 0
    var countF = 0

    var coorenadaLinha = 0
    while (coorenadaLinha < numLinhas) {
        var coordenadaColuna = 0
        while (coordenadaColuna < numColunas) {
            val celula = terreno[coorenadaLinha][coordenadaColuna].first
            if (celula == "J") {
                countJ++
            }
            if (celula == "f") {
                countF++
            }
            coordenadaColuna++
        }
        coorenadaLinha++
    }

    // Só aceita se tiver exatamente 1 J e 1 f
    if (countJ != 1 || countF != 1) {
        return false
    }

    return true
}

fun obtemCoordenadas(entrada: String?): Pair<Int, Int>? {
    if (entrada == null){
        return null
    }  // ou outro valor inválido

    val trimmed = entrada.trim().uppercase()
    if (trimmed.isEmpty() || trimmed.length < 2) {
        return null
    }

    if (!trimmed[0].isDigit() || !trimmed.last().isLetter()) {
        return null
    }

    val numeroStr = trimmed.dropLast(1)
    val letra = trimmed.last()
    val coluna = letra - 'A'

    var numero = 0
    var count = 0
    while (count < numeroStr.length) {
        val digito = numeroStr[count]
        if (digito < '0' || digito > '9') {
            return null
        }
        numero = numero * 10 + (digito - '0')
        count++
    }

    val linha = numero - 1

    // Retorna sempre um Pair (mesmo inválido), como o enunciado exige
    return Pair(linha, coluna)
}

fun validaCoordenadasDentroTerreno(coord: Pair<Int, Int>, numLinhas: Int, numColunas: Int): Boolean {
    val (linha, coluna) = coord
    return linha >= 0 && linha < numLinhas && coluna >= 0 && coluna < numColunas
}

fun validaMovimentoJogador(origem: Pair<Int, Int>, destino: Pair<Int, Int>): Boolean {
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
    val yUm = maxOf(0, linha - 1)
    val xUm = maxOf(0, coluna - 1)
    val yDois = minOf(numLinhas - 1, linha + 1)
    val xDois = minOf(numColunas - 1, coluna + 1)
    return Pair(Pair(yUm, xUm), Pair(yDois, xDois))
}

fun contaMinasPerto(terreno: Array<Array<Pair<String, Boolean>>>, linha: Int, coluna: Int): Int {
    if (terreno.isEmpty() || terreno[0].isEmpty()) {
        return 0
    }

    val numLinhas = terreno.size
    val numColunas = terreno[0].size
    val limites = quadradoAVoltaDoPonto(linha, coluna, numLinhas, numColunas)
    val (y1, x1) = limites.first
    val (y2, x2) = limites.second

    var contagem = 0

    var coordenadaLinha = y1
    while (coordenadaLinha <= y2) {
        var coordenadaColuna = x1
        while (coordenadaColuna <= x2) {
            val mesmaCelula = (coordenadaLinha == linha && coordenadaColuna == coluna)

            if (!mesmaCelula) {
                if (terreno[coordenadaLinha][coordenadaColuna].first == "*") {
                    contagem++
                }
            }

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
        if (terreno[linha][coluna].first == " " && !(linha == 0 && coluna == 0) && !(linha == numLinhas - 1 && coluna == numColunas - 1)) {
            terreno[linha][coluna] = Pair("*", false)
            minasColocadas++
        }
    }
    return terreno
}

fun preencheNumMinasNoTerreno(terreno: Array<Array<Pair<String, Boolean>>>) {
    val linhas = terreno.size
    if (linhas == 0) return
    val colunas = terreno[0].size

    var coordenadaLinha = 0
    while (coordenadaLinha < linhas) {
        var coordenadaColuna = 0
        while (coordenadaColuna < colunas) {
            val conteudo = terreno[coordenadaLinha][coordenadaColuna].first
            if (conteudo != "*" && conteudo != "J" && conteudo != "f") {
                val num = contaMinasPerto(terreno, coordenadaLinha, coordenadaColuna)
                val novoTexto = if (num == 0) " " else num.toString()
                // Força visibilidade = false para números e espaços
                terreno[coordenadaLinha][coordenadaColuna] = Pair(novoTexto, false)
            }
            coordenadaColuna++
        }
        coordenadaLinha++
    }
    // Revela todos os espaços vazios desde o início (visibilidade true)
    var linha = 0
    while (linha < linhas) {
        var coluna = 0
        while (coluna < colunas) {
            val conteudo = terreno[linha][coluna].first
            if (conteudo == " ") {
                terreno[linha][coluna] = Pair(" ", true)
            }
            coluna++
        }
        linha++
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

fun criaTerreno(terreno: Array<Array<Pair<String, Boolean>>>, mostraLegenda: Boolean = true, mostraTudo: Boolean = false): String {
    val numLinhas = terreno.size
    if (numLinhas == 0) return ""
    val numColunas = terreno[0].size

    var resultado = ""

    // Legenda superior
    if (mostraLegenda) {
        resultado += "    "
        var count = 0
        while (count < numColunas) {
            resultado += ('A' + count)
            if (count < numColunas - 1) {
                resultado += "   "
            }
            count++
        }
        resultado += "    \n"
    }

    var linha = 0
    while (linha < numLinhas) {
        if (mostraLegenda) {
            val numStr = (linha + 1).toString()
            if (numStr.length == 1) resultado += " "
            resultado += numStr
            resultado += " "
        }

        var coluna = 0
        while (coluna < numColunas) {
            val (conteudo, visivel) = terreno[linha][coluna]
            val deveMostrar = mostraTudo || visivel
            val simbolo = if (deveMostrar) conteudo else " "
            resultado += " $simbolo "
            if (coluna < numColunas - 1) resultado += "|"
            coluna++
        }
        if(mostraLegenda == true){
            resultado += "   \n"
        }else{
            resultado += "\n"
        }


        if (linha < numLinhas - 1) {
            if (mostraLegenda) resultado += "   "
            var sep = 0
            while (sep < numColunas) {
                resultado += "---"
                if (sep < numColunas - 1) resultado += "+"
                sep++
            }
            if (mostraLegenda == true){
                resultado += "   \n"
            }else{
                resultado += "\n"
            }
        }
        linha++
    }
    var resultadoOfc = ""
    if (mostraLegenda == true){
        resultadoOfc = resultado
    }else{
        resultadoOfc = resultado.dropLast(1)
    }
    return resultadoOfc

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
            } else {
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

fun validaNome(nome: String, tamanhoMinimo: Int = 3): Boolean {
    var espacos = 0
    var contadorAtual = 0
    var primeiraDaPalavra = true
    var cLetra = 0
    while (cLetra < nome.length) {
        val cooord = nome[cLetra]
        if (cooord == ' ') {
            if (contadorAtual < tamanhoMinimo){
                return false
            }
            espacos++
            contadorAtual = 0
            primeiraDaPalavra = true
        } else {
            if (primeiraDaPalavra) {
                if (cooord != cooord.uppercaseChar()){
                    return false
                }
                primeiraDaPalavra = false
            }
            contadorAtual++
        }
        cLetra++
    }
    if (contadorAtual < tamanhoMinimo){
        return false
    }
    espacos++
    return espacos == 2
}

fun lerFicheiroJogo(nomeFicheiro: String, numLinhas: Int, numColunas: Int): Array<Array<Pair<String, Boolean>>>? {

    var caminho = nomeFicheiro.trim()
    if (!caminho.lowercase().endsWith(".txt")) {
        caminho += ".txt"
    }

    val ficheiro = File(caminho)

    if (!ficheiro.exists() || !ficheiro.isFile) {
        println("Ficheiro invalido")
        return null
    }

    val linhasFicheiro: Array<String>
    try {
        val conteudo = ficheiro.readText()
        val linhasTemp = conteudo.split("\n")
        var countValidas = 0

        var index = 0
        while (index < linhasTemp.size) {
            if (linhasTemp[index].trim().isNotEmpty()) {
                countValidas++
            }
            index++
        }

        linhasFicheiro = Array(countValidas) { "" }

        index = 0
        var idx = 0
        while (index < linhasTemp.size) {
            val linha = linhasTemp[index].trim()
            if (linha.isNotEmpty()) {
                linhasFicheiro[idx] = linha
                idx++
            }
            index++
        }

    } catch (e: Exception) {
        println(MENSAGEM_INVALIDA)
        return null
    }

    if (linhasFicheiro.size != numLinhas) {
        println(MENSAGEM_INVALIDA)
        return null
    }

    val matriz = Array(numLinhas) { Array(numColunas) { Pair(" ", false) } }

    var linha = 0
    while (linha < numLinhas) {

        val textoLinha = linhasFicheiro[linha]
        val partes = textoLinha.split(",")

        if (partes.size != numColunas) {
            println(MENSAGEM_INVALIDA)
            return null
        }

        var coluna = 0
        while (coluna < numColunas) {
            val valor = partes[coluna].trim()

            when (valor) {
                "J", "*", "f" -> matriz[linha][coluna] = Pair(valor, false)
                "" -> matriz[linha][coluna] = Pair(" ", false)
                else -> {
                    println(MENSAGEM_INVALIDA)
                    return null
                }
            }

            coluna++
        }

        linha++
    }

    var countJ = 0
    var countF = 0

    linha = 0
    while (linha < numLinhas) {
        var coluna = 0
        while (coluna < numColunas) {
            val valor = matriz[linha][coluna].first
            if (valor == "J"){
                countJ++
            }
            if (valor == "f"){
                countF++
            }
            coluna++
        }
        linha++
    }

    if (countJ != 1 || countF != 1) {
        println(MENSAGEM_INVALIDA)
        return null
    }

    return matriz
}

fun main() {
    println(criaMenu())

    var opcaoValida = true
    while (opcaoValida) {
        val opcao = readln().trim()
        var voltaAoMenu = false

        if (opcao == "1" || opcao == "2") {

            var nome = ""
            var nomeValido = false
            while (!nomeValido) {
                println("Introduz o nome do jogador")
                val input = readln().trim()
                if (validaNome(input)) {
                    nome = input
                    nomeValido = true
                } else {
                    println(MENSAGEM_INVALIDA)
                }
            }

            var mostraLegenda = true
            var legendaValida = false
            while (!legendaValida) {
                println("Mostrar legenda (s/n)?")
                when (readln().trim().lowercase()) {
                    "s" -> {
                        mostraLegenda = true
                        legendaValida = true
                    }
                    "n" -> {
                        mostraLegenda = false
                        legendaValida = true
                    }
                    else -> println(MENSAGEM_INVALIDA)
                }
            }

            val numLinhas = lerNumeroPositivo("Quantas linhas?")
            val numColunas = lerNumeroPositivo("Quantas colunas?")

            var terreno: Array<Array<Pair<String, Boolean>>>? = null

            if (opcao == "1") {
                var numMinas = 1
                var minasValidadas = false
                while (!minasValidadas) {
                    println("Quantas minas (ou enter para o valor por omissao)?")
                    val input = readln().trim()
                    if (!mostraLegenda) println()

                    if (input.isEmpty()) {
                        numMinas = calculaNumeroDeMinas(numLinhas, numColunas)
                        minasValidadas = true
                    } else {
                        var valor = 0
                        var valido = true
                        var i = 0
                        while (i < input.length && valido) {
                            if (input[i] !in '0'..'9') valido = false
                            else valor = valor * 10 + (input[i] - '0')
                            i++
                        }

                        if (valido && validaNumeroDeMinas(numLinhas, numColunas, valor)) {
                            numMinas = valor
                            minasValidadas = true
                        } else {
                            println(MENSAGEM_INVALIDA)
                        }
                    }
                }

                terreno = geraMatrizTerreno(numLinhas, numColunas, numMinas)

            } else {
                println("Qual o ficheiro de jogo a carregar?")
                val caminho = readln().trim()
                if (!mostraLegenda) println()

                terreno = lerFicheiroJogo(caminho, numLinhas, numColunas)
                if (terreno == null) {
                    println(MENSAGEM_INVALIDA)
                    voltaAoMenu = true
                }
            }

            if (!voltaAoMenu) {
                preencheNumMinasNoTerreno(terreno!!)

                var posJogador = Pair(0, 0)
                var encontrouJ = false
                var l = 0
                while (l < numLinhas && !encontrouJ) {
                    var c = 0
                    while (c < numColunas && !encontrouJ) {
                        if (terreno[l][c].first == "J") {
                            posJogador = Pair(l, c)
                            encontrouJ = true
                        }
                        c++
                    }
                    l++
                }

                if (!encontrouJ) {
                    println(MENSAGEM_INVALIDA)
                    voltaAoMenu = true
                }

                if (!voltaAoMenu) {
                    terreno[posJogador.first][posJogador.second] = Pair("J", true)
                    revelaCelulasAoRedor(terreno, posJogador.first, posJogador.second)

                    var jJaSaiuDaOrigem = false

                    var underlyingCurrent = " "
                    var tudoReveladoPermanente = false
                    var ajudas = 1
                    var jogoAtivo = true

                    while (jogoAtivo) {
                        print(criaTerreno(terreno, mostraLegenda, tudoReveladoPermanente))

                        println("\nAinda tens $ajudas ajudas")
                        println("Faltam ${contaNumeroMinasNoCaminho(terreno, posJogador.first, posJogador.second)} minas até ao fim")

                        println("Introduz a celula destino (ex: 2D)")
                        val entrada = readln().trim()

                        // 👉 COMANDO GLOBAL "sair"
                        if (entrada.lowercase() == "sair") {
                            jogoAtivo = false
                            voltaAoMenu = true
                        }

                        if (jogoAtivo) {
                            var processado = false

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
                                processado = true
                            }

                            if (!processado && entrada.lowercase() == "ajuda") {
                                if (ajudas > 0) {
                                    revelaUmaMina(terreno)
                                    ajudas = 0
                                } else {
                                    println(MENSAGEM_INVALIDA)
                                }
                                processado = true
                            }

                            if (!processado) {
                                val destino = obtemCoordenadas(entrada)
                                if (destino == null || !validaCoordenadasDentroTerreno(destino, numLinhas, numColunas)
                                    || !validaMovimentoJogador(posJogador, destino)
                                ) {
                                    println("Movimento invalido.")
                                } else {
                                    val (nl, nc) = destino
                                    val conteudoNovo = terreno[nl][nc].first

                                    if (conteudoNovo == "*") {
                                        terreno[nl][nc] = Pair("*", true)
                                        print(criaTerreno(terreno, mostraLegenda, true))
                                        println(MSG_PERDEU)
                                        jogoAtivo = false
                                    } else if (conteudoNovo == "f") {
                                        print(criaTerreno(terreno, mostraLegenda, true))
                                        println(MSG_GANHOU)
                                        jogoAtivo = false
                                    } else {
                                        val eraVisivel = terreno[nl][nc].second

                                        if (!tudoReveladoPermanente) escondeMatriz(terreno)

                                        terreno[posJogador.first][posJogador.second] =
                                            Pair(underlyingCurrent, false)

                                        if (!jJaSaiuDaOrigem && posJogador == Pair(0, 0)) {
                                            val numero = contaMinasPerto(terreno, 0, 0)
                                            terreno[0][0] = Pair(if (numero == 0) " " else numero.toString(), false)
                                            jJaSaiuDaOrigem = true
                                        }

                                        underlyingCurrent = conteudoNovo
                                        terreno[nl][nc] = Pair("J", true)
                                        posJogador = destino

                                        if (!tudoReveladoPermanente && (!eraVisivel || conteudoNovo == " ")) {
                                            revelaMatriz(terreno, nl, nc)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        } else {
            if (opcao == "0") opcaoValida = false
            else println(MENSAGEM_INVALIDA)
        }

        if (opcaoValida) {
            println(criaMenu())
        }
    }
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
                return  // Para na primeira mina oculta encontrada (ordem linha-coluna)
            }
            coordenadaColuna++
        }
        coordenadaLinha++
    }
    // Se não encontrou mina oculta, não faz nada
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