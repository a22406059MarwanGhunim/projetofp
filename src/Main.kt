import java.io.File
import kotlin.random.Random

const val MENSAGEM_INVALIDA = "Resposta invalida."
const val MSG_GANHOU = "Ganhaste o jogo!"
const val MSG_PERDEU = "Perdeste o jogo!"
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

fun validaTerreno(terreno: Array<Array<Pair<String, Boolean>>>): Boolean{
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
            val novaVisibilidade = if (conteudo == " ") true else true
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
    if (numLinhas == 0){
        return ""
    }
    val numColunas = terreno[0].size
    // Legenda superior
    if (mostraLegenda) {
        print("    ") // 4 espaços
        var count = 0
        while (count < numColunas) {
            print(('A' + count))
            if (count < numColunas - 1){
                print("   ")
            } // 3 espaços
            count++
        }
        print("    ")
        println()
    }
    var linha = 0
    while (linha < numLinhas) {
        if (mostraLegenda) {
            val numStr = (linha + 1).toString()
            if (numStr.length == 1){
                print(" ")
            }
            print(numStr)
            print(" ") // 1 espaço depois do número
        }
        var coluna = 0
        while (coluna < numColunas) {
            val (conteudo, visivel) = terreno[linha][coluna]
            val deveMostrar = mostraTudo || visivel
            val simbolo = if (deveMostrar){
                conteudo
            } else{
                " "
            }
            print(" $simbolo ")
            if (coluna < numColunas - 1){
                print("|")
            }
            coluna++
        }
        println()
        if (linha < numLinhas - 1) {
            if (mostraLegenda){
                print("   ")
            }
            var sep = 0
            while (sep < numColunas) {
                print("---")
                if (sep < numColunas - 1){
                    print("+")
                }
                sep++
            }
            print("   ")
            println()
        }
        linha++
    }
    return ""
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
        println(MENSAGEM_INVALIDA)
        return null
    }

    val linhasFicheiro: Array<String> = try {
        ficheiro.readLines().toTypedArray()
    } catch (e: Exception) {
        println(MENSAGEM_INVALIDA)
        return null
    }

    if (linhasFicheiro.size != numLinhas) {
        println(MENSAGEM_INVALIDA)
        return null
    }

    val matriz = Array(numLinhas) { i ->
        val linhaTexto = linhasFicheiro[i].trim()
        val partes = linhaTexto.split(",").map { it.trim() }.toTypedArray()

        if (partes.size != numColunas) {
            println(MENSAGEM_INVALIDA)
            return null
        }

        Array(numColunas) { j ->
            val valor = partes[j]
            when (valor) {
                "J" -> Pair("J", true)
                "f" -> Pair("f", true)
                "*" -> Pair("*", false)
                "" -> Pair(" ", false)
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
    var i = 0
    while (i < numLinhas) {
        var j = 0
        while (j < numColunas) {
            val celula = matriz[i][j].first
            if (celula == "J") countJ++
            if (celula == "f") countF++
            j++
        }
        i++
    }

    if (countJ != 1 || countF != 1) {
        println(MENSAGEM_INVALIDA)
        return null
    }

    return matriz
}

fun cicloJogo(
    terreno: Array<Array<Pair<String, Boolean>>>,
    mostraLegenda: Boolean,
    posJogadorInicial: Pair<Int, Int>,
    underlyingInicial: String
) {
    var posJogador = posJogadorInicial
    var underlyingCurrent = underlyingInicial
    var tudoReveladoPermanente = false
    var ajudasRestantes = 1  // Começa com 1 ajuda

    while (true) {
        criaTerreno(terreno, mostraLegenda, tudoReveladoPermanente)

        val minasFaltam = contaNumeroMinasNoCaminho(terreno, posJogador.first, posJogador.second)
        println("Ainda tens $ajudasRestantes ajudas")
        println("Faltam $minasFaltam minas até ao fim")

        println("Introduz a celula destino (ex: 2D")
        val entrada = readln().trim()

        // Tratamento da ajuda
        if (entrada.lowercase() == "ajuda") {
            if (ajudasRestantes > 0) {
                revelaUmaMina(terreno)
                ajudasRestantes = 0
            }
            continue
        }

        if (entrada.lowercase() == CHEAT_CODE) {
            tudoReveladoPermanente = true
            var coordenadaLinha = 0
            while (coordenadaLinha < terreno.size) {
                var coordenadaColuna = 0
                while (coordenadaColuna < terreno[0].size) {
                    terreno[coordenadaLinha][coordenadaColuna] = Pair(terreno[coordenadaLinha][coordenadaColuna].first, true)
                    coordenadaColuna++
                }
                coordenadaLinha++
            }
            continue
        }

        val destino = obtemCoordenadas(entrada)
        if (destino == null || !validaCoordenadasDentroTerreno(destino, terreno.size, terreno[0].size)) {
            println(MENSAGEM_INVALIDA)
            continue
        }

        if (!validaMovimentoJogador(posJogador, destino)) {
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

        // Revela ao redor se necessário
        if (!tudoReveladoPermanente) {
            if (!eraVisivel || conteudoNovo == " ") {
                revelaCelulasAoRedor(terreno, novaL, novaC)
            }
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
        println("Mostrar legenda (s/n)?")
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
        if (input.isEmpty()) {
            numMinas = calculaNumeroDeMinas(numLinhas, numColunas)
            break
        }

        var valor = 0
        var valido = true
        var konta = 0
        while (konta < input.length && valido) {
            if (input[konta] !in '0'..'9'){
                valido = false
            }
            else{
                valor = valor * 10 + (input[konta] - '0')
            }
            konta++
        }

        if (valido && validaNumeroDeMinas(numLinhas, numColunas, valor)) {
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

    // Chama o ciclo de jogo (loop extraído)
    cicloJogo(terreno, mostraLegenda, Pair(0, 0), " ")
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
                    println("Mostrar legenda (s/n)?")
                    when (readln().trim().lowercase()) {
                        "s" -> { mostraLegenda = true; break }
                        "n" -> { mostraLegenda = false; break }
                        else -> println(MENSAGEM_INVALIDA)
                    }
                }

                // Linhas e colunas
                val numLinhas = lerNumeroPositivo("Quantas linhas?")
                val numColunas = lerNumeroPositivo("Quantas colunas?")

                // Pergunta o nome do ficheiro
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
                var i = 0
                while (i < numLinhas && !encontrouJ) {
                    var j = 0
                    while (j < numColunas && !encontrouJ) {
                        if (terreno[i][j].first == "J") {
                            posJogador = Pair(i, j)
                            encontrouJ = true
                        }
                        j++
                    }
                    i++
                }

                if (!encontrouJ) {
                    println(MENSAGEM_INVALIDA)
                    continue
                }

                // Coloca o J visível
                terreno[posJogador.first][posJogador.second] = Pair("J", true)

                // Revela ao redor do J inicial
                revelaCelulasAoRedor(terreno, posJogador.first, posJogador.second)

                // Chama o mesmo ciclo de jogo
                cicloJogo(terreno, mostraLegenda, posJogador, " ")
            }
            "0" -> return
            else -> println(MENSAGEM_INVALIDA)
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