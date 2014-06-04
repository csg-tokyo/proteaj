# ProteaJ

強力なユーザ定義演算子を持つJavaサブセット言語

コンパイラの使い方
---

### コンパイラ自体のビルド

```bash
ant -f proteaj.xml
```

Java8かそれより新しいJavaが必要です。
必要に応じて、proteaj.properties を変更して下さい。

### 準備

```bash
./pjc pjlib/proteaj/lang/*.pj
```

最低限のライブラリをコンパイルします。

### コンパイル

```bash
./pjc test/print/*.pj
```

例えばこんな感じです。
コンパイラは bin ディレクトリにクラスファイルを生成します。
pjc はクラスパスを指定して proteaj.Compiler を実行するだけのシェルスクリプトです。
そのため、直接 java コマンドを叩いて実行することもできます。

### 実行

```bash
./pj print.Main
```

pj は単に bin にクラスパスを通して java コマンドを実行するだけのシェルスクリプトなので、

```bash
java -classpath bin print.Main
```

と完全に等価です。

言語仕様
---
ユーザ定義演算子およびJava 1.4相当の機能がサポートされています。
ただし、内部クラスや無名クラスはサポートしていません。

### 演算子定義

演算子は演算子モジュール内で定義します。
演算子モジュールは operators キーワードにより定義します。

```proteaj
public operators PrintOperators {
  public void "p" _ (String str) {
    System.out.println(str);
  }
}
```

演算子定義はメソッド定義と似ており、

```
{修飾子} 返り値の型 パターン 引数リスト [優先度] ボディ
```

の形式で記述します。
例えば先程の例の場合、
public はアクセス修飾子であり、void が返り値の型、"p" _ がパターンで、(String str) が引数リストです。

#### 修飾子

演算子の修飾子として記述できるのは、
public, nonassoc, rassoc, readas, strictfp, synchronized です。
nonassoc, rassoc は演算子の結合性を示しており、デフォルトは左結合です。
readas はユーザ定義リテラルを表現するための機能で、これがついた演算子のパターンはリテラル(トークン)レベルの文法であるとみなされます。

#### パターン

演算子のパターンは基本的には名前とオペランドで表現します。
例えば、先程の例では "p" が名前で、str がオペランドです。
オペランド名は仮引数名と一致するか、アンダースコアでなければなりません必要があります。

名前およびオペランドの個数や並び順は自由です。
名前のない演算子やオペランドのない演算子も定義可能です。

より便利な記法として、ProteaJ はオプションおよび繰り返し記法を提供しています。
オプションは省略可能なオペランドを表し、デフォルト引数とともに利用します。

```proteaj
public void "p" _? (String str = "\n") {
  System.out.println(str);
}
```

繰り返し記法は0回以上の繰り返し(*) と1回以上の繰り返し (+) の二種類があり、可変長引数とともに用います。

```proteaj
public void "p" _+ (String... strs) {
  for (int i = 0; i < strs.length; i++)
    System.out.println(strs[i]);
}
```

また、この記法では繰り返しの区切り文字を指定することもできます。

```proteaj
public void "p" _+(",") (String... strs) {
  for (int i = 0; i < strs.length; i++)
    System.out.println(strs[i]);
}
```

より発展的な機能として、ProteaJでは述語をパターン中に記述することができます。
述語はいわゆる先読みの機能で、次に現れる式を制限することができます。
述語にはand述語 (&) とnot述語 (!) の2種類があります。
and述語は次に現れる式が指定した型の式として読むことが可能であるよう制限します。
not述語は次に現れる式が指定した型の式として読むことができない式であるよう制限します。

例えば、

```proteaj
public void "p" !Identifier _ (String str) {
  System.out.println(str);
}
```

は "p" の次に識別子が来ないことを指定しています。

```proteaj
String hoge = "hoge";
p "hoge";     // OK
p hoge;       // Error
p "" + hoge;  // OK
```

#### 優先度

ProteaJ では結合順序と解析順序の 2種類の演算子の優先順序が存在します。
演算子定義で記述する優先度は結合順序です。
優先度は非負の整数で表され、省略した場合の優先度は0となります。
値が大きいほど結合が強いことを示します。
例えば、足し算と掛け算の定義は以下のようになります。

```proteaj
public int _ "+" _ (int left, int right) : priority = 900 { ... }
public int _ "*" _ (int left, int right) : priority = 1000 { ... }
```

解析順序は結合順序が同じ演算子のどちらを優先して使用するかを示す順序です。
この順序は演算子の定義順序によって決定します。
例えば、

```proteaj
void "if" _ "then" _ "else" _ (boolean c, lazy void e1, lazy void e2) { ... }
void "if" _ "then" _ (boolean c, lazy void e) { ... }
```

の場合、先に定義した if_then_else_ 演算子が if_then_ 演算子より優先して利用されます。
より特殊な演算子を先に定義する、と考えて下さい。

#### 名前渡しオペランド (引数の遅延評価)

引数リストの型名の前に lazy キーワードをつけることで、対応するオペランドの実引数が遅延評価されるようになります。

```proteaj
public void "if" _ "then" _ (boolean c, lazy void e) {
  if (c) e;
}
```

ただし、現在の実装は完全ではなく、引数部での代入計算を行った場合の振る舞いは保証できません。
また、オペランドの実引数として書くことができるのは式であり、文ではない点にも注意して下さい。

#### 演算子の返り値の型と継承の制限

演算子はその返り値の型が期待されるような式部分でのみ利用できます。
例えば、

```proteaj
public readas Alphabets _+ (Alphabet... as) { ... }
public readas Alphabet "a" () : priority = 100 { ... }
public readas Alphabet "z" () : priority = 100 { ... }
```

と定義した場合、

```proteaj
Alphabets as = alphabets;  // OK
String str = alphabets;    // Error!
```

のように使えます。

また、演算子はその返り値の型のスーパータイプが期待されている部分でも使うことができます。
しかし、これは時々不都合な場合があります。
例えば、先ほどの演算子は次のように使うこともできてしまいます。

```proteaj
Object obj = alphabets;  // OK (!?)
```

このような挙動が望ましくない場合は多く考えられるので、
制限する機能を用意しています。

```proteaj
public readas [Alphabets] _+ (Alphabet... as) { ... }
public readas [Alphabet] "a" () : priority = 100 { ... }
public readas [Alphabet] "z" () : priority = 100 { ... }
```

のように定義することで、これらの演算子は期待される型がちょうど返り値の型と同じ場合にのみ利用可能となります。

```proteaj
Alphabets as = alphabets;  // OK
String str = alphabets;    // Error!
Object obj = alphabets;    // Error!
```

また、

```proteaj
public readas [Alphabets to AbstractAlphabets] _+ (Alphabet... as) { ... }
public readas [Alphabet] "a" () : priority = 100 { ... }
public readas [Alphabet] "z" () : priority = 100 { ... }
```

のように定義すると、Alphabets <: T <: AbstractAlphabets を満たす T 型を期待するような式部分で利用可能となります。


### 演算子の使い方

演算子を利用するにはその演算子の演算子モジュールを using 節で宣言する必要があります。
using 節は import 節の後に記述します。
モジュール名はフルパスで指定して下さい。

```proteaj
package test;
import print.*;
using print.PrintOperators;

class Test {
  void foo() {
    p "Hello, world!";
  }
  ...
}
```

proteaj.lang.PrimitiveOperators, proteaj.lang.PrimitiveReadasOperators, proteaj.lang.StringOperators の3つの演算子モジュールは暗黙的に using されています。
もし、このような演算子モジュールを利用したくないようなときは、unusing 節を using 節の後に書いて下さい。

#### using の順序と優先順位

演算子の結合順序は演算子モジュール内で閉じています。
全体の結合順序は演算子モジュールの using 順序によって決定します。
先にusing節が書いてある演算子モジュールのほうが結合が強く、後に書いてある演算子モジュールのほうが結合が弱くなります。

#### 演算子の利用可能箇所

ProteaJ の演算子は期待される型が分かる部分でしか使うことができません。
例えば、代入文の右辺では利用できますが、左辺では利用できません。
ProteaJ の演算子が利用できないのは、
代入文の左辺、メソッドやフィールドアクセスのレシーバ部分 (a.b の a)、Javaのキャスト構文の引数部です。
それ以外の式部分では演算子を利用することができます。

### Java の機能

ProteaJ は概ね Java 1.4 相当の機能をサポートしています。
ただし、内部クラスや匿名クラスはサポートしていません。(他にもサポートを忘れている機能があるかもしれません)

ProteaJ の演算子は期待される型が分かる部分でしか使えないため、通常のJavaのキャスト構文では引数部で演算子を利用できません。
そのため、ProteaJ では通常のJavaのキャストの他に、独自のキャスト構文を用意しています。

```proteaj
Object o = ...;
String s0 = (String)o;     // 通常のキャスト
String s1 = (Object ->)o;  // 独自のキャスト構文 その1
String s2 = (<- Object)o;  // 独自のキャスト構文 その2
String s3 = (Object -> String)o;  // 独自のキャスト構文 その3
String s4 = (String <- Object)o;  // 独自のキャスト構文 その4
```

ProteaJ では、Javaと異なり式文の式はvoid型でなければなりません。
そのため、void 以外の値を返す式を式文で用いる場合はキャストを利用して下さい。

```proteaj
int printAndReturn (int a) {
  System.out.println(a);
  return a;
}
void foo () {
  (<- int) printAndReturn (1 + 2);
}
```

ログ
---
### 2014/6/4
 - 演算子の利用可能な型にスコープを付ける機能を追加
 - オペランドをアンダースコアで記述可能に
 - synchronized ブロックをサポート
 - switch, break, continue をサポート
 - 複数フィールドの同時宣言をサポート
 - float, double, クラスリテラルをサポート
 - 16進数リテラルをサポート
 - 8進・16進文字コードを文字列リテラル中で利用可能に
 - ビット演算子をサポート
 - instanceof をサポート
 - 配列初期化構文をサポート
 - C言語風の配列変数宣言をサポート
 - int 以外のプリミティブ型の演算子を追加
 - プリミティブ型のワイドニング変換を一部サポート
 - エラーメッセージをもう少しまともに
 - 多数のバグを修正
 - コンパイラのパフォーマンスを10倍以上改善

その他
---

### 論文

Composable user-defined operators that can express user-defined literals
http://dl.acm.org/citation.cfm?id=2577092&CFID=286284435&CFTOKEN=93977891

### 開発者

Kazuhiro Ichikawa
ichikawa@csg.ci.i.u-tokyo.ac.jp