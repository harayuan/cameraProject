
public class Main {

	public static void main(String[] args) {
		double rR = 35.59;
		double rG = 9.32;
		double rB = 15.28;
		double gR = 5.7;
		double gG = 24.86;
		double gB = 16.96;
		double bR = 8.82;
		double bG = 16.35;
		double bB = 35.95;

		double[][] measureData = {
		{
		1, rR, rG, rB, Math.pow(rR, 2), rR * rG, Math.pow(rG, 2), rR * rB, rG * rB, Math.pow(rB, 2)
		}, {
		1, gR, gG, gB, Math.pow(gR, 2), gR * gG, Math.pow(gG, 2), gR * gB, gG * gB, Math.pow(gB, 2)
		}, {
		1, bR, bG, bB, Math.pow(bR, 2), bR * bG, Math.pow(bG, 2), bR * bB, bG * bB, Math.pow(bB, 2)
		}
		};
		double[][] targetData = {
		{
		255, 0, 0
		}, {
		0, 255, 0
		}, {
		0, 0, 255
		}
		};

		MyMatrix target;
		MyMatrix measure;
		MyMatrix transMeasure;
		MyMatrix mulMeasure;
		MyMatrix invMeasure = null;
		MyMatrix calMatrix = null;

		target = new MyMatrix(targetData);
		System.out.println("target: " + target.getNrows() + "x" + target.getNcols());
		target.printMatrix();
		measure = new MyMatrix(measureData);
		System.out.println("\nmeasure:" + measure.getNrows() + "x" + measure.getNcols());
		measure.printMatrix();
		transMeasure = MatrixMathematics.transpose(measure);
		System.out.println("\ntransMeasure:" + transMeasure.getNrows() + "x" + transMeasure.getNcols());
		transMeasure.printMatrix();
		mulMeasure = MatrixMathematics.multiply(transMeasure, measure);
		System.out.println("\nmulMeasure:" + mulMeasure.getNrows() + "x" + mulMeasure.getNcols());
		mulMeasure.printMatrix();
		
		try {
			invMeasure = MatrixMathematics.inverse(mulMeasure);
			System.out.println("\ninvMeasure:" + invMeasure.getNrows() + "x" + invMeasure.getNcols());
			invMeasure.printMatrix();
		} catch (NoSquareException e) {
			e.printStackTrace();
		}
		
		calMatrix = MatrixMathematics.multiply(MatrixMathematics.multiply(invMeasure, transMeasure), target);
		System.out.println("\ncalMatrix:" + calMatrix.getNrows() + "x" + calMatrix.getNcols());
		calMatrix.printMatrix();
		
		
		/*
		calMatrix = MatrixMathematics.multiply(invMeasure, target);
		System.out.println("\ncalMatrix:");
		calMatrix.printMatrix();

		double[][] data = {
			{
			25.58, 22.8, 33.53
			}
		};
		
		MyMatrix targetMatrixData = new MyMatrix(data);
		MyMatrix targetMatrix = MatrixMathematics.multiply(targetMatrixData, calMatrix);
		System.out.println("\ntargetMatrix:");
		targetMatrix.printMatrix();
		*/
	}

}
