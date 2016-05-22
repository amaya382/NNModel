# NNModel

NNModel is a **crude implementation** of neural network


## Usage

1. Get MNIST dataset from [kaggle](https://www.kaggle.com/c/digit-recognizer/data) and put it in the root of NNModel as `train.csv`
2. Import this project to IDEs and exec on IDEs, or exec `sbt test` on terminal


## APIs

Start calc from `nnmodel.components.Network` and chain following methods

### Add layers

* input layer: `.inputLayer($1, $2)`
  * `$1`: # of dimensions: `Int`
  * [opt]`$2`: default settings: `nnmodel.Settings`
    * default: `weight == normal dist`, `bias == 0`, `learning rate == 0.05`

* hidden layer: `.hiddenLayer($1, $2)`
  * `$1`: # of dimenstions: `Int`
  * [opt]`$2`: activation function: `nnmodel.components.ActivationFunction`
    * default: sigmoid

* output layer: `.outputLayer($1, $2)`
  * `$1`: # of dimenstions: `Int`
  * [opt]`$2`: activation function: `nnmodel.components.ActivationFunction`
    * default: softmax

### Train

* train: `.train($1, $2)`
  * `$1`: input data: `Seq[Double]`
  * `$2`: expected result: `Seq[Double]`

### Run

* run: `.run($1)`
  * `$1`: input data: `Seq[Double]`

### Sample

see `nnmodel.Test` in `test`


## Others

* OOP for readability
* no dependence
* using several variables for minimum optimization
* not parallelized
* maybe implement
  * decreasing learning rate automatically
  * dropout
  * batch traning
  * parallelizing
