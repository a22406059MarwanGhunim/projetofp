fun criaMenu(): String? {
    return "\nBem vindo ao Campo DEISIado\n\n1 - Novo Jogo\n2 - Ler Jogo\n0 - Sair\n"
}

fun validaNome(nome: String, minLetras: Int = 3): Boolean {
    var espacosEncontrados = 0
    var indice = 0
    var letrasNaPalavraAtual = 0
    var primeiraLetraDaPalavra = true

    while (indice < nome.length) {
        val caractere = nome[indice]
        if (caractere == ' ') {
            if (letrasNaPalavraAtual < minLetras) return false
            espacosEncontrados++
            letrasNaPalavraAtual = 0
            primeiraLetraDaPalavra = true
        } else {
            if (primeiraLetraDaPalavra) {
                if (caractere != caractere.uppercaseChar()) return false
                primeiraLetraDaPalavra = false
            }
            letrasNaPalavraAtual++
        }
        indice++
    }
    if (letrasNaPalavraAtual < minLetras) return false
    espacosEncontrados++
    return espacosEncontrados == 2
}

fun validaNumeroDeMinas(minas: Int, linhas: Int, colunas: Int): Boolean {
    if (minas < 0) return false
    val celulasLivres = linhas * colunas - 2
    return minas <= celulasLivres
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
        if (coluna < colunas - 1) legenda += "   "
        coluna++
    }
    return legenda
}

fun criaTerreno(linhas: Int, colunas: Int, minas: Int, mostraLegenda: Boolean): String {
    var terreno = ""
    var minasPorColocar = minas
    var linhaAtual = 1

    while (linhaAtual <= linhas) {
        if (mostraLegenda) {
            val numeroLinha = if (linhaAtual < 10) " $linhaAtual " else "$linhaAtual "
            terreno += numeroLinha
        }

        var colunaAtual = 1
        while (colunaAtual <= colunas) {
            val simbolo = when {
                linhaAtual == 1 && colunaAtual == 1 -> "J"
                linhaAtual == 1 && colunaAtual == colunas -> "f"
                linhaAtual == 1 && minasPorColocar > 0 -> {
                    minasPorColocar--
                    "*"
                }
                else -> " "
            }
            if (colunaAtual == 1) terreno += " "
            terreno += simbolo
            if (colunaAtual < colunas) {
                terreno += " | "
            } else {
                terreno += " "
            }
            colunaAtual++
        }
        linhaAtual++
    }

    if (mostraLegenda) {
        terreno = criaLegenda(colunas) + "\n" + terreno
    }
    return terreno
}

const val MENSAGEM_INVALIDA = "Resposta invalida."
const val NAO_IMPLEMENTADO = "NAO IMPLEMENTADO"

fun main() {
    while (true) {
        println(criaMenu())
        val opcao = readln()

        if (opcao == "1") {
            var nomeValido = false
            var nomeJogador = ""
            while (!nomeValido) {
                println("Introduz o nome do jogador")
                nomeJogador = readln()
                if (validaNome(nomeJogador)) {
                    nomeValido = true
                } else {
                    println(MENSAGEM_INVALIDA)
                }
            }

            var mostraLegenda = false
            var legendaDefinida = false
            while (!legendaDefinida) {
                println("Mostrar legenda (s/n)?")
                val resposta = readln().lowercase()
                if (resposta == "s") {
                    mostraLegenda = true
                    legendaDefinida = true
                } else if (resposta == "n") {
                    mostraLegenda = false
                    legendaDefinida = true
                } else {
                    println(MENSAGEM_INVALIDA)
                }
            }

            var numeroLinhas = 0
            while (numeroLinhas != 1) {
                println("Quantas linhas?")
                val entrada = readln()
                if (entrada == "1") {
                    numeroLinhas = 1
                } else {
                    println(MENSAGEM_INVALIDA)
                }
            }

            var numeroColunas = 0
            while (numeroColunas < 1) {
                println("Quantas colunas?")
                val entrada = readln()
                var valor = 0
                var entradaValida = entrada.isNotEmpty()
                var indice = 0
                while (indice < entrada.length && entradaValida) {
                    val digito = entrada[indice]
                    if (digito < '0' || digito > '9') entradaValida = false
                    else valor = valor * 10 + (digito - '0')
                    indice++
                }
                if (entradaValida && valor >= 1) {
                    numeroColunas = valor
                } else {
                    println(MENSAGEM_INVALIDA)
                }
            }

            var numeroMinas = -1
            var minasDefinidas = false
            while (!minasDefinidas) {
                println("Quantas minas (ou enter para o valor por omissao)?")
                val entrada = readln()
                if (entrada.isEmpty()) {
                    numeroMinas = calculaNumeroDeMinas(numeroLinhas, numeroColunas)
                    minasDefinidas = true
                } else {
                    var valor = 0
                    var entradaValida = true
                    var indice = 0
                    while (indice < entrada.length && entradaValida) {
                        val digito = entrada[indice]
                        if (digito < '0' || digito > '9') entradaValida = false
                        else valor = valor * 10 + (digito - '0')
                        indice++
                    }
                    if (entradaValida && validaNumeroDeMinas(valor, numeroLinhas, numeroColunas)) {
                        numeroMinas = valor
                        minasDefinidas = true
                    } else {
                        println(MENSAGEM_INVALIDA)
                    }
                }
            }

            println(criaTerreno(numeroLinhas, numeroColunas, numeroMinas, mostraLegenda))
            return
        }

        if (opcao == "2") {
            println(NAO_IMPLEMENTADO)
        } else if (opcao == "0") {
            return
        } else {
            println(MENSAGEM_INVALIDA)
        }
    }
}